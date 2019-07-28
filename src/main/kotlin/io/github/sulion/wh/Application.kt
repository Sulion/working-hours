package io.github.sulion.wh

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.sulion.wh.api.DataFormatException
import io.github.sulion.wh.api.WorkingHoursTransformer
import io.github.sulion.wh.impl.DefaultWorkingHoursTransformer
import io.github.sulion.wh.impl.LaxRestaurantValidator
import io.github.sulion.wh.model.Restaraunt
import io.github.sulion.wh.util.DayOfWeekDeserializer
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import java.time.DayOfWeek

fun Application.main() {
    val workingHoursTransformer: WorkingHoursTransformer = DefaultWorkingHoursTransformer()
    val validator = LaxRestaurantValidator()
    install(DefaultHeaders)
    install(CallLogging)
    install(StatusPages) {
        exception<DataFormatException> { ex ->
            call.respond(HttpStatusCode.BadRequest, ex.message!!)
        }
        exception<JsonParseException> {
            call.respond(HttpStatusCode.UnsupportedMediaType, "The message is not a well-formed JSON")
        }
        exception<UnsupportedMediaTypeException> {
            call.respond(HttpStatusCode.UnsupportedMediaType, "The message is not a well-formed JSON or a Content-type header is missing")
        }
        exception<Exception> { cause ->
            call.respond(HttpStatusCode.InternalServerError, "Unexpected server error")
            throw cause // Rethrow to log it
        }
    }
    install(ContentNegotiation) {
        jackson {
            registerModule(
                    SimpleModule().apply {
                        addKeyDeserializer(DayOfWeek::class.java, DayOfWeekDeserializer())
                    }
            )
        }
    }
    install(Routing) {
        get("/") {
            call.respondText("Use POST method to convert working hours data to human-readable text", ContentType.Text.Plain)
        }
        post("/") {
            val request = call.receive<Restaraunt>()
            validator.validate(request)
            call.respondText { workingHoursTransformer.toHumanFriendlyFormat(request) }
        }
    }
}
