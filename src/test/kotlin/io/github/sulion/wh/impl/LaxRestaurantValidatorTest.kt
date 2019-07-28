package io.github.sulion.wh.impl

import io.github.sulion.wh.api.DataFormatException
import io.github.sulion.wh.model.Opening
import io.github.sulion.wh.model.OpeningType.close
import io.github.sulion.wh.model.OpeningType.open
import io.github.sulion.wh.model.Restaraunt
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.TUESDAY

internal class LaxRestaurantValidatorTest {
    @Test
    fun testOverlappingOpening() {
        val monday = listOf(
                Opening(open, 32400),
                Opening(close, 57600),
                Opening(open, 39600),
                Opening(close, 82800)
        )
        val restaurant = Restaraunt(mutableMapOf(MONDAY to monday))
        val validator = LaxRestaurantValidator()
        assertThrows<DataFormatException.OverlappingOpenings> {
            validator.validate(restaurant)
        }
    }

    @Test
    fun testDoubleCloseAndDoubleOpen() {
        val monday = listOf(
                Opening(open, 32400),
                Opening(close, 57600),
                Opening(close, 39600),
                Opening(close, 82800)
        )
        val restaurant = Restaraunt(mutableMapOf(MONDAY to monday))
        val validator = LaxRestaurantValidator()
        assertThrows<DataFormatException.UnexpectedBoundary> {
            validator.validate(restaurant)
        }
        val tuesday = listOf(
                Opening(open, 32400),
                Opening(open, 57600),
                Opening(close, 39600),
                Opening(close, 82800)
        )
        val restaurantTwo = Restaraunt(mutableMapOf(TUESDAY to tuesday))

        assertThrows<DataFormatException.UnexpectedBoundary> {
            validator.validate(restaurantTwo)
        }
    }

    @Test
    fun testNoOpeningWhenNullYesterday() {
        val monday = listOf(
                Opening(close, 3600),
                Opening(open, 32400),
                Opening(close, 39600),
                Opening(open, 57600),
                Opening(close, 82800)
        )
        val restaurant = Restaraunt(mutableMapOf(MONDAY to monday))
        val validator = LaxRestaurantValidator()
        assertThrows<DataFormatException.InconsistentWeek> {
            validator.validate(restaurant)
        }
    }

    @Test
    fun testNoClosingWhenNullTomorrow() {
        val monday = listOf(
                Opening(open, 32400),
                Opening(close, 39600),
                Opening(open, 57600)
        )
        val restaurant = Restaraunt(mutableMapOf(MONDAY to monday))
        val validator = LaxRestaurantValidator()
        assertThrows<DataFormatException.InconsistentWeek> {
            validator.validate(restaurant)
        }
    }

    @Test
    fun testNoClosingWhenEmptyTomorrow() {
        val monday = listOf(
                Opening(open, 32400),
                Opening(close, 39600),
                Opening(open, 57600)
        )
        val restaurant = Restaraunt(mutableMapOf(MONDAY to monday, TUESDAY to listOf()))
        val validator = LaxRestaurantValidator()
        assertThrows<DataFormatException.InconsistentWeek> {
            validator.validate(restaurant)
        }
    }


    @Test
    fun testNoClosingWhenInconsistentTomorrow() {
        val monday = listOf(
                Opening(open, 32400),
                Opening(close, 39600),
                Opening(open, 57600)
        )
        val tuesday = listOf(
                Opening(open, 32400),
                Opening(close, 39600),
                Opening(open, 57600)
        )
        val restaurant = Restaraunt(mutableMapOf(MONDAY to monday, TUESDAY to tuesday))
        val validator = LaxRestaurantValidator()
        assertThrows<DataFormatException.InconsistentWeek> {
            validator.validate(restaurant)
        }
    }

    @Test
    fun testNoOpeningWhenInconsistentYesterday() {
        val monday = listOf(
                Opening(open, 32400),
                Opening(close, 39600),
                Opening(open, 57600),
                Opening(close, 82800)
        )
        val tuesday = listOf(
                Opening(close, 3600),
                Opening(open, 32400),
                Opening(close, 39600),
                Opening(open, 57600)
        )
        val restaurant = Restaraunt(mutableMapOf(MONDAY to monday, TUESDAY to tuesday))
        val validator = LaxRestaurantValidator()
        assertThrows<DataFormatException.InconsistentWeek> {
            validator.validate(restaurant)
        }
    }
}