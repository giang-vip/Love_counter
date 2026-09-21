package com.app.love_counter.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "name_test_results")
data class NameTestResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val yourName: String,
    val partnerName: String,
    val percent: Int,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
