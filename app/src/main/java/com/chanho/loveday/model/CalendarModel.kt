package com.chanho.loveday.model

data class CalendarModel(
    val id: Int?,
    val specialDate: String?,
    val content: String?,
    val writer: Writer?,
    val createdAt: String?,
    val updatedAt: String?
)

data class Writer(
    val id: Int?,
    val writer: String?,
    val partner: String?,
    val token: String?,
    val createdAt: String?,
    val updatedAt: String?
)