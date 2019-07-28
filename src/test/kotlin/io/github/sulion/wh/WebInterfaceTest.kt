package io.github.sulion.wh

import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.restassured.RestAssured
import io.restassured.RestAssured.get
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class WebInterfaceTest {
    companion object {

        private var serverStarted = false

        private lateinit var server: ApplicationEngine

        @BeforeAll
        @JvmStatic
        fun startServer() {
            if (!serverStarted) {
                server = embeddedServer(
                        Netty,
                        environment = applicationEngineEnvironment {
                            module {
                                main()
                            }
                            connector {
                                host = "0.0.0.0"
                                port = 8080
                            }
                        }
                )
                server.start()
                serverStarted = true

                RestAssured.baseURI = "http://localhost"
                RestAssured.port = 8080
                Runtime.getRuntime().addShutdownHook(Thread { server.stop(0, 0, TimeUnit.SECONDS) })
            }
        }
    }

    @Test
    fun testGetSuggestion() {
        get("/")
                .then()
                .statusCode(200)
                .assertThat()
                .body(equalTo("Use POST method to convert working hours data to human-readable text"))
    }

    @Test
    fun testPostIncorrectRequest() {
        given().body("Incorrect string")
                .contentType(ContentType.JSON)
                .`when`()
                .post("/")
                .then()
                .statusCode(415)
                .assertThat()
                .body(equalTo("The message is not a well-formed JSON"))
    }

    @Test
    fun testCorrectDataWithoutHeader() {
        given().body(javaClass.getResourceAsStream("impl/full-week-from-task.json"))
                .`when`()
                .post("/")
                .then()
                .statusCode(415)
    }

    @Test
    fun testCorrectData_SunnyDay() {
        given().body(javaClass.getResourceAsStream("impl/full-week-from-task.json"))
                .contentType(ContentType.JSON)
                .`when`()
                .post("/")
                .then()
                .statusCode(200)
                .assertThat()
                .body(equalTo("Monday: Closed\n" +
                        "Tuesday: 10 AM - 6 PM\n" +
                        "Wednesday: Closed\n" +
                        "Thursday: 10 AM - 6 PM\n" +
                        "Friday: 10 AM - 1 AM\n" +
                        "Saturday: 10 AM - 1 AM\n" +
                        "Sunday: 12 PM - 9 PM\n"))
    }
}
