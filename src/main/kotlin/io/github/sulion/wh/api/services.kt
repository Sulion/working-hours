package io.github.sulion.wh.api

import io.github.sulion.wh.model.RestaurantData

interface WorkingHoursTransformer {
    fun toHumanFriendlyFormat(input: RestaurantData): String
}

interface RestaurantValidator {
    fun validate(restaurant: RestaurantData)
}