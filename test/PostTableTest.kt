import cn.nikeo.server.database.tables.Category
import cn.nikeo.server.database.tables.Post
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import kotlin.test.*

class PostTableTest {
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
            val single = Post.select { Post.id eq 2 }.single()
            val title = single[Post.title]
            println(single[Post.date])
            assertEquals(title, "Lambda")
        }
    }

    @Test
    fun insert_success_test() {
        transaction(db = db) {
            // print sql to std-out
            addLogger(StdOutSqlLogger)
            val id = Post.insert {
                it[title] = "Lambda"
                it[date] = DateTime.now()
                it[tags] = "Lambda"
                it[path] = "/posts/cpp/lambda.md"
                it[categoryId] = Category.select { Category.name eq "C/C++" }.single()[Category.id]
            } get Post.id
            assertEquals(id.value, 2)
        }
    }

    @Test
    fun insert_fail_test() {
        assertFails {
            transaction(db = db) {
                // print sql to std-out
                addLogger(StdOutSqlLogger)
                return@transaction Post.insert {
                    it[title] = "command:ps"
                    it[date] = DateTime.now()
                    it[tags] = "shell, ps"
                    it[path] = "/posts/linux/command_ps.md"
                    it[categoryId] = entityId("", Category)
                } get Post.id
            }
        }
    }

    @AfterTest
    fun onAfterTest() {
        println("end")
    }
}
