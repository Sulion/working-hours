package io.github.sulion.wh.impl

import io.github.sulion.wh.model.Opening
import io.github.sulion.wh.model.OpeningType.close
import io.github.sulion.wh.model.OpeningType.open
import io.github.sulion.wh.model.Restaraunt
import io.github.sulion.wh.util.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.DayOfWeek.*

const val ONLY_FRI_AND_SAT: String = "only-fri-and-sat.json"
const val FRI_SAT_UNEVEN_HOURS: String = "fri-and-sat-with-uneven-hours.json"
const val FULL_WEEK_EXAMPLE = "full-week-from-task.json"

internal class DefaultWorkingHoursTransformerTest {

    @Test
    fun toHumanFriendlyFormat() {
        val restaraunt = readValue<Restaraunt>(this.javaClass.getResourceAsStream(ONLY_FRI_AND_SAT))
        val transformer = DefaultWorkingHoursTransformer()
        val timetables = transformer.toHumanFriendlyFormat(listOf(restaraunt))
        assertEquals(1, timetables.size)
        val first = timetables.get(0)
        assertEquals("Monday: Closed", first.workingHours[MONDAY])
        assertEquals("Tuesday: Closed", first.workingHours[TUESDAY])
        assertEquals("Friday: 6 PM - 1 AM", first.workingHours[FRIDAY])
        assertEquals("Saturday: 9 AM - 11 AM, 4 PM - 11 PM", first.workingHours[SATURDAY])
    }

    @Test
    fun testWithMinutesInTimeTable() {
        val restaraunt = readValue<Restaraunt>(this.javaClass.getResourceAsStream(FRI_SAT_UNEVEN_HOURS))
        val transformer = DefaultWorkingHoursTransformer()
        val timetables = transformer.toHumanFriendlyFormat(listOf(restaraunt))
        assertEquals(1, timetables.size)
        val first = timetables.get(0)
        assertEquals("Monday: Closed", first.workingHours[MONDAY])
        assertEquals("Tuesday: Closed", first.workingHours[TUESDAY])
        assertEquals("Friday: 5.30 PM - 1.30 AM", first.workingHours[FRIDAY])
        assertEquals("Saturday: 9 AM - 11 AM, 4 PM - 10.30 PM", first.workingHours[SATURDAY])
    }

    @Test
    fun testSingleDayConversion() {
        val transformer = DefaultWorkingHoursTransformer()
        val monday = listOf(
                Opening(open, 32400),
                Opening(close, 39600),
                Opening(open, 57600),
                Opening(close, 82800)
        )
        val mondayString = transformer.makeDailyString(MONDAY, monday, null)
        assertEquals("Monday: 9 AM - 11 AM, 4 PM - 11 PM", mondayString)
    }

    @Test
    fun testFullExample() {
        val restaraunt = readValue<Restaraunt>(this.javaClass.getResourceAsStream(FULL_WEEK_EXAMPLE))
        val transformer = DefaultWorkingHoursTransformer()
        val timetables = transformer.toHumanFriendlyFormat(listOf(restaraunt))
        assertEquals(1, timetables.size)
        val first = timetables.get(0)
        println(first)
        assertEquals(7, first.workingHours.size)
        assertEquals("Monday: Closed", first.workingHours[MONDAY])
        assertEquals("Wednesday: Closed", first.workingHours[WEDNESDAY])
        assertEquals("Thursday: 10 AM - 6 PM", first.workingHours[THURSDAY])
        assertEquals("Saturday: 10 AM - 1 AM", first.workingHours[SATURDAY])
    }
}