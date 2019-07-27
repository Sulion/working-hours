package io.github.sulion.wh.api;

import io.github.sulion.wh.model.Restaraunt
import io.github.sulion.wh.model.Timetable

interface WorkingHoursTransformer {
    fun toHumanFriendlyFormat(input: List<Restaraunt>): List<Timetable>

}