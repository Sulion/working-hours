package io.github.sulion.wh

import io.github.sulion.wh.api.WorkingHoursTransformer
import io.github.sulion.wh.impl.DefaultWorkingHoursTransformer
import io.github.sulion.wh.model.Restaraunt
import io.github.sulion.wh.util.readValue
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post

fun Application.main() {
    val workingHoursTransformer: WorkingHoursTransformer = DefaultWorkingHoursTransformer()
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/") {
            call.respondText("Use POST method to convert working hours data to human-readable text", ContentType.Text.Plain)
        }
        post("/") {
            val request = readValue<Restaraunt>(call.receive<String>())

            call.respondText {
                workingHoursTransformer.toHumanFriendlyFormat(request)
            }
        }
    }
}
