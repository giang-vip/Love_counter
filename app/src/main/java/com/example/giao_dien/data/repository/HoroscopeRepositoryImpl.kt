package com.example.giao_dien.data.repository

import com.example.giao_dien.R
import com.example.giao_dien.domain.model.HoroscopeItem
import com.example.giao_dien.domain.model.HoroscopeResult
import com.example.giao_dien.domain.repository.HoroscopeRepository
import kotlin.math.abs

class HoroscopeRepositoryImpl : HoroscopeRepository {

    private val horoscopes = listOf(
        HoroscopeItem("aries", "Aries", R.drawable.ic_aries),
        HoroscopeItem("taurus", "Taurus", R.drawable.ic_taurus),
        HoroscopeItem("gemini", "Gemini", R.drawable.ic_gemini),
        HoroscopeItem("cancer", "Cancer", R.drawable.ic_cancer),
        HoroscopeItem("leo", "Leo", R.drawable.ic_leo),
        HoroscopeItem("virgo", "Virgo", R.drawable.ic_virgo),
        HoroscopeItem("libra", "Libra", R.drawable.ic_libra),
        HoroscopeItem("scorpio", "Scorpio", R.drawable.ic_scorpio),
        HoroscopeItem("sagittarius", "Sagittarius", R.drawable.ic_sagittarius),
        HoroscopeItem("capricorn", "Capricorn", R.drawable.ic_capricorn),
        HoroscopeItem("aquarius", "Aquarius", R.drawable.ic_aquarius),
        HoroscopeItem("pisces", "Pisces", R.drawable.ic_pises)
    )

    override fun getAllHoroscopes(): List<HoroscopeItem> = horoscopes

    override fun getHoroscopeById(id: String): HoroscopeItem {
        return horoscopes.find { it.id.equals(id, ignoreCase = true) } ?: horoscopes.first()
    }

    override fun calculateMatchPercent(h1: HoroscopeItem, h2: HoroscopeItem): Int {
        val key = "${h1.id}_${h2.id}"
        val hash = abs(key.hashCode())
        return (hash % 101)
    }

    override fun getLoveDescription(percent: Int): String {
        return when (percent) {
            in 0..5 -> "Everything can become chaotic because it is difficult for them to trust each other..."
            in 6..20 -> "In a relationship, both partners may try to change for each other, but eventually they may feel exhausted..."
            in 21..35 -> "Overall, both are stubborn but in different ways, lacking patience and understanding..."
            in 36..50 -> "The couple is not too intense, nor too sweet and melting, they are together cultivating love..."
            in 51..70 -> "A beautiful, peaceful and harmonious love affair like a calm sea..."
            in 71..90 -> "Love each other in our own way, protect each other from the smallest things..."
            else -> "They have many things in common, attracted to each other by harmony and passion..."
        }
    }

    override fun calculateHoroscopeResult(h1Id: String, h2Id: String): HoroscopeResult {
        val h1 = getHoroscopeById(h1Id)
        val h2 = getHoroscopeById(h2Id)
        val percent = calculateMatchPercent(h1, h2)
        val description = getLoveDescription(percent)
        return HoroscopeResult(
            horoscope1 = h1,
            horoscope2 = h2,
            percent = percent,
            description = description
        )
    }
}