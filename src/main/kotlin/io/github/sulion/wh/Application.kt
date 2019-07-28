package io.github.sulion.wh

import com.fasterxml.jackson.core.JsonParseException
import io.github.sulion.wh.api.DataFormatException
import io.github.sulion.wh.api.WorkingHoursTransformer
import io.github.sulion.wh.impl.DefaultWorkingHoursTransformer
import io.github.sulion.wh.impl.LaxRestaurantValidator
import io.github.sulion.wh.model.Restaraunt
import io.github.sulion.wh.util.readValue
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post

fun Application.main() {
    val workingHoursTransformer: WorkingHoursTransformer = DefaultWorkingHoursTransformer()
    val validator = LaxRestaurantValidator()
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/") {
            call.respondText("Use POST method to convert working hours data to human-readable text", ContentType.Text.Plain)
        }
        post("/") {
            try {
                val request = readValue<Restaraunt>(call.receive<String>())
                validator.validate(request);
                call.respondText {
                    workingHoursTransformer.toHumanFriendlyFormat(request)
                }
            } catch (ex: DataFormatException) {
                call.respond(HttpStatusCode.BadRequest, ex.message!!)
            } catch (ex: JsonParseException) {
                call.respond(HttpStatusCode.UnsupportedMediaType, "The message is not a well-formed JSON")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Unexpected server error")
            }
        }
    }
}
