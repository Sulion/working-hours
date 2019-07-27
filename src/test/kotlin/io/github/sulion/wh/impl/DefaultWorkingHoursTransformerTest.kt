package io.github.sulion.wh.impl

import io.github.sulion.wh.model.Restaraunt
import io.github.sulion.wh.util.readValue
import org.junit.jupiter.api.Test

const val ONLY_FRI_AND_SAT: String = "only-fri-and-sat.json"

internal class DefaultWorkingHoursTransformerTest {

    @Test
    fun toHumanFriendlyFormat() {
        val restaraunt = readValue<Restaraunt>(this.javaClass.getResourceAsStream(ONLY_FRI_AND_SAT))
        println(restaraunt.toString())
    }
}