package com.example.giao_dien.domain.model

data class HoroscopeResult(
    val horoscope1: HoroscopeItem,
    val horoscope2: HoroscopeItem,
    val percent: Int,
    val description: String
)