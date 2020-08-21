package cn.nikeo.server.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.datetime

object Posts : IntIdTable(name = "post") {
    val title = varchar("title", 100)
    val date = datetime("date")
    val tags = varchar("tags", 100).nullable()
    val path = varchar("path", 255)
    val enable = bool("enable")
    val categoryId = integer("category_id").entityId().references(
        Categories.id,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE,
        fkName = "fk_category"
    )
}