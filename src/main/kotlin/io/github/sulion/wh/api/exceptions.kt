package io.github.sulion.wh.api

sealed class DataFormatException(message: String) : Exception(message) {
    class UnexpectedBoundary(message: String) : DataFormatException(message)
    class OverlappingOpenings(message: String) : DataFormatException(message)
    class InconsistentWeek(message: String) : DataFormatException(message)
}