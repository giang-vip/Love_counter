package com.app.love_counter.domain.model

data class HoroscopeResult(
    val horoscope1: HoroscopeItem,
    val horoscope2: HoroscopeItem,
    val percent: Int,
    val description: String
)