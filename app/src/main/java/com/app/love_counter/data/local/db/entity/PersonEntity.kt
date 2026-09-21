package com.app.love_counter.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "person_table")
data class PersonEntity(
    @PrimaryKey val isMale: Boolean,
    val name: String,
    val gender: String,
    val birthday: String,
    val avatarUri: String?
)