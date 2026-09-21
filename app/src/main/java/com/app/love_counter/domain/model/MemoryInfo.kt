package com.app.love_counter.domain.model

data class MemoryInfo(
    val id: Long = 0,
    val title: String,
    val date: String,
    val imageUri: String? = null
)