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

internal class DefaultWorkingHoursTransformerTest {

    @Test
    fun toHumanFriendlyFormat() {
        val restaraunt = readValue<Restaraunt>(this.javaClass.getResourceAsStream(ONLY_FRI_AND_SAT))
        println(restaraunt.toString())
        val transformer = DefaultWorkingHoursTransformer()
        val timetables = transformer.toHumanFriendlyFormat(listOf(restaraunt))
        assertEquals(1, timetables.size)
        val first = timetables.get(0)
        println(first)
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
}