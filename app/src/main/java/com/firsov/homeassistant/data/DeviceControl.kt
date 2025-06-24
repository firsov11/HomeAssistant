package com.firsov.homeassistant.data

data class DeviceControl(
    val radar: Boolean = false,
    val vent: Boolean = false,
    val sensor_co: Boolean = false
)
