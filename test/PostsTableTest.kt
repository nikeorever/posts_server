import cn.nikeo.server.database.tables.Categories
import cn.nikeo.server.database.tables.Posts
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import kotlin.test.*

class PostsTableTest {
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
            val single = Posts.select { Posts.id eq 2 }.single()
            val title = single[Posts.title]
            println(single[Posts.date])
            assertEquals(title, "Lambda")
        }
    }

    @Test
    fun insert_success_test() {
        transaction(db = db) {
            // print sql to std-out
            addLogger(StdOutSqlLogger)
            val id = Posts.insert {
                it[title] = "Lambda"
                it[date] = DateTime.now()
                it[tags] = "Lambda"
                it[path] = "/posts/cpp/lambda.md"
                it[categoryId] = Categories.select { Categories.name eq "C/C++" }.single()[Categories.id]
            } get Posts.id
            assertEquals(id.value, 2)
        }
    }

    @Test
    fun insert_fail_test() {
        assertFails {
            transaction(db = db) {
                // print sql to std-out
                addLogger(StdOutSqlLogger)
                Posts.insert {
                    it[title] = "command:ps"
                    it[date] = DateTime.now()
                    it[tags] = "shell, ps"
                    it[path] = "/posts/linux/command_ps.md"
                    it[categoryId] = entityId("", Categories)
                }
            }
        }
    }

    @AfterTest
    fun onAfterTest() {
        println("end")
    }
}
