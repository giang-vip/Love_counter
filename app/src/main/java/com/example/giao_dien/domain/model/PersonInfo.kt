package com.example.giao_dien.domain.model

data class PersonInfo(
    val name: String,
    val gender: GenderType,
    val birthday: String,
    val avatarUri: String? = null
)