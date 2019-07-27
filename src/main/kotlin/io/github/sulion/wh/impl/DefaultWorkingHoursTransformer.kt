package io.github.sulion.wh.impl

import io.github.sulion.wh.api.WorkingHoursTransformer
import io.github.sulion.wh.model.Opening
import io.github.sulion.wh.model.OpeningType.close
import io.github.sulion.wh.model.OpeningType.open
import io.github.sulion.wh.model.Restaraunt
import io.github.sulion.wh.model.Timetable
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class DefaultWorkingHoursTransformer : WorkingHoursTransformer {
    companion object {
        val timeFormatterWithMinutes = DateTimeFormatter.ofPattern("h.mm a")
        val timeFormatterWithoutMinutes = DateTimeFormatter.ofPattern("h a")
    }

    override fun toHumanFriendlyFormat(input: List<Restaraunt>): List<Timetable> =
            input.map { transformRestaurant(it) }

    private fun transformRestaurant(restaraunt: Restaraunt): Timetable =
            DayOfWeek.values()
                    .associate {
                        it to makeDailyString(
                                it,
                                restaraunt.workingHours[it],
                                restaraunt.workingHours[it.tomorrow()]
                        )
                    }
                    .let { Timetable(it) }


    internal fun makeDailyString(dayOfWeek: DayOfWeek, today: List<Opening>?, tomorrow: List<Opening>?): String =
            when {
                today == null -> dayOfWeek.printClosed()
                today.isEmpty() -> dayOfWeek.printClosed()
                today.first().type == close -> makeDailyString(dayOfWeek, today.takeLast(today.size - 1), tomorrow)
                today.last().type == open -> makeDailyString(dayOfWeek, today + tomorrow!!.first(), null)
                else -> "${dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)}: ${toRanges(today)}"
            }


    private fun toRanges(openings: List<Opening>): String =
            openings.windowed(2, 2)
                    .map { "${it.first().to12hDate()} - ${it.last().to12hDate()}" }
                    .joinToString(", ")

    private fun DayOfWeek.tomorrow() = when (this) {
        DayOfWeek.SUNDAY -> DayOfWeek.MONDAY
        else -> DayOfWeek.of(value + 1)
    }

    private fun Opening.to12hDate() = LocalTime.ofSecondOfDay(value)
            .let {
                when {
                    it.minute == 0 -> timeFormatterWithoutMinutes.format(it)
                    else -> timeFormatterWithMinutes.format(it)
                }
            }

    private fun DayOfWeek.printClosed(): String =
            "${getDisplayName(TextStyle.FULL, Locale.ENGLISH)}: Closed"

}