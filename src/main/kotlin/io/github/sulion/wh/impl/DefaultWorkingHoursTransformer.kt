package io.github.sulion.wh.impl

import io.github.sulion.wh.api.WorkingHoursTransformer
import io.github.sulion.wh.model.Restaraunt
import io.github.sulion.wh.model.Timetable

class DefaultWorkingHoursTransformer : WorkingHoursTransformer {

    override fun toHumanFriendlyFormat(input: List<Restaraunt>): List<Timetable> {
        return emptyList()

    }
}