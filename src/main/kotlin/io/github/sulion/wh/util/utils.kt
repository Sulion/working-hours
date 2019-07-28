package io.github.sulion.wh.util

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer
import java.time.DayOfWeek


class DayOfWeekDeserializer : KeyDeserializer() {
    override fun deserializeKey(key: String, ctxt: DeserializationContext?): Any =
            DayOfWeek.valueOf(key.toUpperCase())
}
