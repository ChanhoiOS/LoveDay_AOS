package com.chanho.loveday.model

data class MemoModel(
    val id: Int?,
    val title: String?,
    val content: String?,
    val writer: Writer?,
    val createAt: String?,
    val updatedAt: String?
)