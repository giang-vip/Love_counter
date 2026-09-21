package com.app.love_counter.domain.model

data class CoupleInfo(
    val maleInfo: PersonInfo,
    val femaleInfo: PersonInfo,
    val loveStartDate: String
)