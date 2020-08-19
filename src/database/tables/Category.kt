package cn.nikeo.server.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Category : IntIdTable(name = "category") {
    val name = varchar("name", 100)
    val enable = bool("enable")
}