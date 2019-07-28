package io.github.sulion.wh.impl

import io.github.sulion.wh.model.Opening
import io.github.sulion.wh.model.OpeningType.close
import io.github.sulion.wh.model.OpeningType.open
import io.github.sulion.wh.model.Restaraunt
import io.github.sulion.wh.util.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.DayOfWeek.MONDAY

const val ONLY_FRI_AND_SAT: String = "only-fri-and-sat.json"
const val FRI_SAT_UNEVEN_HOURS: String = "fri-and-sat-with-uneven-hours.json"
const val FULL_WEEK_EXAMPLE = "full-week-from-task.json"

internal class DefaultWorkingHoursTransformerTest {

    @Test
    fun toHumanFriendlyFormat() {
        val restaraunt = readValue<Restaraunt>(this.javaClass.getResourceAsStream(ONLY_FRI_AND_SAT))
        val transformer = DefaultWorkingHoursTransformer()
        val timetables = transformer.toHumanFriendlyFormat(restaraunt)
                .split("\n").filter { it.isNotBlank() }
        assertEquals(7, timetables.size)
        assertEquals("Monday: Closed", timetables[0])
        assertEquals("Tuesday: Closed", timetables[1])
        assertEquals("Friday: 6 PM - 1 AM", timetables[4])
        assertEquals("Saturday: 9 AM - 11 AM, 4 PM - 11 PM", timetables[5])
    }

    @Test
    fun testWithMinutesInTimeTable() {
        val restaraunt = readValue<Restaraunt>(this.javaClass.getResourceAsStream(FRI_SAT_UNEVEN_HOURS))
        val transformer = DefaultWorkingHoursTransformer()
        val timetables = transformer.toHumanFriendlyFormat(restaraunt)
                .split("\n").filter { it.isNotBlank() }
        assertEquals(7, timetables.size)

        assertEquals("Monday: Closed", timetables[0])
        assertEquals("Tuesday: Closed", timetables[1])
        assertEquals("Friday: 5.30 PM - 1.30 AM", timetables[4])
        assertEquals("Saturday: 9 AM - 11 AM, 4 PM - 10.30 PM", timetables[5])
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
        val timetables = transformer.toHumanFriendlyFormat(restaraunt).split("\n").filter { it.isNotBlank() }
        assertEquals(7, timetables.size)

        assertEquals("Monday: Closed", timetables[0])
        assertEquals("Wednesday: Closed", timetables[2])
        assertEquals("Thursday: 10 AM - 6 PM", timetables[3])
        assertEquals("Saturday: 10 AM - 1 AM", timetables[5])
    }
}