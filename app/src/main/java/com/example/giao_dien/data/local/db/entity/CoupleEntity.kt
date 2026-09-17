package com.example.giao_dien.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "couple_table")
data class CoupleEntity(
    @PrimaryKey val id: Int = 1,
    val loveStartDate: String?,
    val backgroundUri: String?
)