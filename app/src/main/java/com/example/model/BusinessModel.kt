package com.example.model

data class BusinessModel(
    val name_of_business: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour_start: Int,
    val min_start: Int,
    val hour_end: Int,
    val min_end: Int,
    var priority: Int = 0,
    var alarm: Int = 0,
//    var isAlarmChanged: Boolean = false,
//    var isPriority: Boolean = false,
    var id: Int? = null,) {

    fun invertPriority() {
        priority = if (priority == 1) 0 else 1
    }

    fun invertAlarm() {
        alarm = if (alarm == 1) 0 else 1
    }
}


