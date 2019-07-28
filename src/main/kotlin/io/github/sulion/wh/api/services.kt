package io.github.sulion.wh.api

import io.github.sulion.wh.model.Restaraunt

interface WorkingHoursTransformer {
    fun toHumanFriendlyFormat(input: Restaraunt): String
}

interface RestaurantValidator {
    fun validate(restaurant: Restaraunt)
}