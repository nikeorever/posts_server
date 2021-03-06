import cn.nikeo.server.database.tables.Categories
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CategoriesTableTest {
    private lateinit var db: Database;

    @BeforeTest
    fun onBeforeTest() {
        db = Database.connect("jdbc:mysql://localhost:3306/nikeo_posts", user = "root", password = "[Ningkun521]")
        println("start")
    }

    @Test
    fun data_test() {
        transaction(db = db) {
            // print sql to std-out
            addLogger(StdOutSqlLogger)
            val linuxId = Categories.select { Categories.name eq "Linux" }.single()[Categories.id].value
            assertEquals(linuxId, 4)
        }
    }

    @AfterTest
    fun onAfterTest() {
        println("end")
    }
}
