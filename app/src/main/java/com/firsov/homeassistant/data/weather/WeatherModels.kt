package com.firsov.homeassistant.data.weather

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt: Long,
    val main: MainData
)

data class MainData(
    val pressure: Float,
    val grnd_level: Float?
)