package com.firsov.homeassistant.data

data class DeviceTriggered (
    val co_alert: Boolean = false,
    val radar_alert: Boolean = false,
    val vent_alert: Boolean = false
)