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
    var id: Int? = null,
)


