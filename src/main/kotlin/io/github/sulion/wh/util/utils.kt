package io.github.sulion.wh.util

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.InputStream
import java.time.DayOfWeek

object JsonConfiguration {
    val jacksonMapper = jacksonObjectMapper()
            .apply {
                registerModule(
                        SimpleModule().apply {
                            addKeyDeserializer(DayOfWeek::class.java, DayOfWeekDeserializer())
                        }
                )
            }
}

class DayOfWeekDeserializer : KeyDeserializer() {
    override fun deserializeKey(key: String, ctxt: DeserializationContext?): Any =
            DayOfWeek.valueOf(key.toUpperCase())
}

inline fun <reified T> readValue(source: InputStream): T =
        JsonConfiguration.jacksonMapper.readValue(source)