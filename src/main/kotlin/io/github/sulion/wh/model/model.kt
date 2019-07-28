package io.github.sulion.wh.model

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.DayOfWeek

enum class OpeningType { open, close }

data class Opening(
        val type: OpeningType,
        val value: Long
)

data class Restaraunt(@JsonIgnore val workingHours: MutableMap<DayOfWeek, List<Opening>>) {
    @JsonAnyGetter
    fun readWorkingHours(): Map<DayOfWeek, List<Opening>> =
            workingHours

    @JsonAnySetter
    fun writeWorkingHours(day: String, openings: List<Opening>) {
        workingHours.put(DayOfWeek.valueOf(day.toUpperCase()), openings)
    }

    fun fetchYesterday(key: DayOfWeek): List<Opening>? =
            workingHours[key.minus(1)]

    fun fetchTomorrow(key: DayOfWeek): List<Opening>? =
            workingHours[key.plus(1)]
}

data class Timetable(val workingHours: Map<DayOfWeek, String>)
