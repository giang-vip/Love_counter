package com.example.giao_dien.domain.model

data class CoupleInfo(
    val maleInfo: PersonInfo,
    val femaleInfo: PersonInfo,
    val loveStartDate: String
)