package com.example.giao_dien.domain.repository

import com.example.giao_dien.domain.model.HoroscopeItem
import com.example.giao_dien.domain.model.HoroscopeResult

interface HoroscopeRepository {
    fun getAllHoroscopes(): List<HoroscopeItem>
    fun getHoroscopeById(id: String): HoroscopeItem
    fun calculateMatchPercent(h1: HoroscopeItem, h2: HoroscopeItem): Int
    fun getLoveDescription(percent: Int): String
    fun calculateHoroscopeResult(h1Id: String, h2Id: String): HoroscopeResult
}