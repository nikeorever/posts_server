package cn.nikeo.server.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

fun <T> internalTransaction(connection: MySQLJdbcConnection, statement: Transaction.() -> T): T {
    val db = Database.connect(connection.url(), user = connection.user(), password = connection.password())
    return transaction(db = db, statement = statement)
}

abstract class MySQLJdbcConnection {
    companion object {
        val docker: MySQLJdbcConnection = Docker();
        val dev: MySQLJdbcConnection = Dev();
    }

    abstract fun host(): String
    abstract fun port(): String
    abstract fun user(): String
    abstract fun password(): String
    abstract fun database(): String
    fun url(): String = "jdbc:mysql://${host()}:${port()}/${database()}"

    private class Docker : MySQLJdbcConnection() {
        override fun host(): String = System.getenv("DOCKER_MYSQL_HOST")

        override fun port(): String = System.getenv("DOCKER_MYSQL_PORT")

        override fun user(): String = System.getenv("DOCKER_MYSQL_USER")

        override fun password(): String = System.getenv("DOCKER_MYSQL_PASSWORD")

        override fun database(): String = System.getenv("DOCKER_MYSQL_DATABASE")
    }

    private class Dev : MySQLJdbcConnection() {
        override fun host(): String = "localhost"

        override fun port(): String = "3306"

        override fun user(): String = "root"

        override fun password(): String = "[Ningkun521]"

        override fun database(): String = "nikeo_posts"
    }
}
