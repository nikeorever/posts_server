package cn.nikeo.server.services

import cn.nikeo.server.database.DatabaseFactory.dbQuery
import cn.nikeo.server.database.tables.Posts
import cn.nikeo.server.models.Post
import io.ktor.util.*
import org.jetbrains.exposed.sql.*

@KtorExperimentalAPI
class PostService {

    suspend fun getPosts(
        categoryId: Int?,
        enable: Boolean?,
        limit: Int?,
        offset: Long?,
        order: SortOrder
    ): List<Post> = dbQuery {
        Posts.select {
            val categoryIdOp = if (categoryId == null) Op.TRUE else Posts.categoryId eq categoryId
            val enableOp = if (enable == null) Op.TRUE else Posts.enable eq enable
            categoryIdOp and enableOp
        }.orderBy(Posts.date, order = order).also { query ->
            if (limit != null && offset != null && limit > 0 && offset > 0) {
                query.limit(limit, offset)
            }
        }.map(::toPost)
    }

    private fun toPost(row: ResultRow): Post =
        Post(
            id = row[Posts.id].value,
            title = row[Posts.title],
            date = row[Posts.date],
            tags = row[Posts.tags],
            path = row[Posts.path],
            enable = row[Posts.enable],
            categoryId = row[Posts.categoryId].value
        )
}