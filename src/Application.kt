package cn.nikeo.server

import cn.nikeo.server.database.MySQLJdbcConnection
import cn.nikeo.server.database.internalTransaction
import cn.nikeo.server.database.tables.Category
import cn.nikeo.server.database.tables.Post
import cn.nikeo.server.gson.DateTimeTypeAdapter
import cn.nikeo.server.model.BaseResponse
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val mysqlJdbcConnection: MySQLJdbcConnection
    when {
        isDev -> {
            // Do things only in dev
            mysqlJdbcConnection = MySQLJdbcConnection.dev
        }
        isProd -> {
            // Do things only in prod
            mysqlJdbcConnection = MySQLJdbcConnection.docker
        }
        else -> throw IllegalArgumentException("dev or prod required!")
    }
    // Do things for all the environments
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()

            serializeNulls()

            registerTypeAdapter(DateTime::class.java, DateTimeTypeAdapter())

            serializeSpecialFloatingPointValues()

            setLenient()
            setVersion(0.1)
        }
    }

    routing {
        get("/v1/categories") {
            val enable = call.parameters["enable"]?.toBoolean()
            val categories = internalTransaction(mysqlJdbcConnection) {
                return@internalTransaction Category.select {
                    if (enable == null) Op.TRUE else Category.enable eq enable
                }.map {
                    mapOf(
                        "id" to it[Category.id].value,
                        "name" to it[Category.name],
                        "enable" to it[Category.enable]
                    )
                }
            }
            call.respond(
                BaseResponse(
                    success = true,
                    message = "Get categories successfully",
                    data = categories
                )
            )
        }

        get("/v1/posts") {
            val categoryId = call.parameters["category_id"]?.toIntOrNull()
            val enable = call.parameters["enable"]?.toBoolean()

            val limit = call.parameters["limit"]?.toIntOrNull()
            val offset = call.parameters["offset"]?.toLongOrNull()

            val order = call.parameters["order"].let { order ->
                when (order.orEmpty().toUpperCase()) {
                    "ASC" -> SortOrder.ASC
                    "DESC" -> SortOrder.DESC
                    else -> SortOrder.DESC
                }
            }

            val posts = internalTransaction(mysqlJdbcConnection) {
                return@internalTransaction Post.select {
                    val categoryIdOp = if (categoryId == null) Op.TRUE else Post.categoryId eq categoryId
                    val enableOp = if (enable == null) Op.TRUE else Post.enable eq enable
                    categoryIdOp and enableOp
                }.orderBy(Post.date, order = order)
                    .also { query ->
                        if (limit != null && offset != null && limit > 0 && offset > 0) {
                            query.limit(limit, offset)
                        }
                    }
                    .map {
                        mapOf(
                            "id" to it[Post.id].value,
                            "title" to it[Post.title],
                            "date" to it[Post.date],
                            "tags" to it[Post.tags],
                            "path" to it[Post.path],
                            "enable" to it[Post.enable],
                            "category_id" to it[Post.categoryId].value
                        )
                    }
            }
            call.respond(
                BaseResponse(
                    success = true,
                    message = "Get posts successfully",
                    data = posts
                )
            )
        }
    }
}

@KtorExperimentalAPI
val Application.envKind
    get() = environment.config.property("ktor.environment").getString()

@KtorExperimentalAPI
val Application.isDev
    get() = envKind == "dev"

@KtorExperimentalAPI
val Application.isProd
    get() = envKind != "dev"