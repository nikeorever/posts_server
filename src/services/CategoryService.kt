package cn.nikeo.server.services

import cn.nikeo.server.database.DatabaseFactory.dbQuery
import cn.nikeo.server.database.tables.Categories
import cn.nikeo.server.models.Category
import io.ktor.util.*
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

@KtorExperimentalAPI
class CategoryService {

    suspend fun getCategories(enable: Boolean?): List<Category> = dbQuery {
        Categories.select {
            if (enable == null) Op.TRUE else Categories.enable eq enable
        }.map(::toCategory)
    }

    private fun toCategory(row: ResultRow): Category =
        Category(
            id = row[Categories.id].value,
            name = row[Categories.name],
            enable = row[Categories.enable]
        )
}