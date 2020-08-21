package cn.nikeo.server

import cn.nikeo.server.database.DatabaseFactory
import cn.nikeo.server.gson.DateTimeTypeAdapter
import cn.nikeo.server.models.BaseResponse
import cn.nikeo.server.services.CategoryService
import cn.nikeo.server.services.PostService
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.SortOrder
import org.joda.time.DateTime

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    val categoryService = CategoryService()
    val postService = PostService()

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

    DatabaseFactory.init()

    routing {
        get("/v1/categories") {
            val enable = call.parameters["enable"]?.toBoolean()
            val categories = categoryService.getCategories(enable = enable)
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

            val posts = postService.getPosts(
                categoryId = categoryId,
                enable = enable,
                limit = limit,
                offset = offset,
                order = order
            )
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