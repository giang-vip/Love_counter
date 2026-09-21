package com.app.love_counter.domain.model

data class PersonInfo(
    val name: String,
    val gender: GenderType,
    val birthday: String,
    val avatarUri: String? = null
)