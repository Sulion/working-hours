package io.github.sulion.wh.impl

import io.github.sulion.wh.api.DataFormatException
import io.github.sulion.wh.api.RestaurantValidator
import io.github.sulion.wh.model.Opening
import io.github.sulion.wh.model.OpeningType.close
import io.github.sulion.wh.model.OpeningType.open
import io.github.sulion.wh.model.RestaurantData
import java.time.DayOfWeek

/**
 * Will not throw if some of the days are omitted: they will be considered closed days.
 * Otherwise will check:
 * - open is always followed by closed, and vice versa
 * - each next time mark within one day is greater than the previous one
 * - each days opens by open, except when the previous day is finished by open
 */
class LaxRestaurantValidator : RestaurantValidator {
    override fun validate(restaurant: RestaurantData) =
            restaurant.workingHours.forEach {
                if (it.value.isNotEmpty()) {
                    checkDayBoundaries(
                            it,
                            restaurant.fetchYesterday(it.key),
                            restaurant.fetchTomorrow(it.key)
                    )
                    checkOpenCloseSequence(it)
                    checkNoOverlapping(it)
                }
            }

    private fun checkDayBoundaries(
            today: Map.Entry<DayOfWeek, List<Opening>>,
            yesterday: List<Opening>?,
            tomorrow: List<Opening>?
    ) {
        val firstDayRecord = today.value.first().type
        val lastDayRecord = today.value.last().type
        if (
                (firstDayRecord == close) &&
                (yesterday == null || yesterday.isEmpty() || yesterday.last().type != open)
        ) {
            throw DataFormatException.InconsistentWeek("No opening for ${today.key}'s closing")
        }

        if (
                (lastDayRecord == open) &&
                (tomorrow == null || tomorrow.isEmpty() || tomorrow.first().type != close)
        ) {
            throw DataFormatException.InconsistentWeek("No closing for ${today.key}'s opening")
        }
    }

    private fun checkNoOverlapping(entry: Map.Entry<DayOfWeek, List<Opening>>) =
            entry.value.fold(0L) { acc, opening ->
                if (opening.value <= acc) {
                    throw DataFormatException.OverlappingOpenings(generateOverlappingOpeningsMessage(entry.key, opening))
                } else {
                    opening.value
                }
            }

    private fun checkOpenCloseSequence(entry: Map.Entry<DayOfWeek, List<Opening>>) =
            entry.value.takeLast(entry.value.size - 1)
                    .fold(entry.value.first().type) { acc, opening ->
                        if (opening.type == acc) {
                            throw DataFormatException.UnexpectedBoundary(generateUnexpectedBoundaryMsg(entry.key, opening))
                        } else {
                            opening.type
                        }
                    }

    private fun generateUnexpectedBoundaryMsg(key: DayOfWeek, opening: Opening): String =
            "$key contains error: opening $opening describes unexpected boundary"

    private fun generateOverlappingOpeningsMessage(dayOfWeek: DayOfWeek, opening: Opening): String =
            "$dayOfWeek contains error: opening $opening overlaps with the previous one"
}