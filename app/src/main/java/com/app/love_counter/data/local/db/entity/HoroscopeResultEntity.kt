package com.app.love_counter.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "horoscope_results")
data class HoroscopeResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val horoscope1Id: String,
    val horoscope2Id: String,
    val percent: Int,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
