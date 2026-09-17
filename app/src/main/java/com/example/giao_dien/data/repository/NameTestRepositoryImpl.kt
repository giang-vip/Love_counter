package com.example.giao_dien.data.repository

import com.example.giao_dien.domain.model.NameTestResult
import com.example.giao_dien.domain.repository.NameTestRepository
import kotlin.math.abs

class NameTestRepositoryImpl : NameTestRepository {

    override fun calculateNameTestResult(yourName: String, partnerName: String): NameTestResult {
        val name1 = yourName.trim()
        val name2 = partnerName.trim()

        // Deterministic score based on names
        val combined = "${name1.lowercase()}_${name2.lowercase()}"
        val hash = abs(combined.hashCode())
        val percent = (hash % 101)  // Score ranges from 1% to 100%

        val description = when (percent) {
            in 0..20 -> "Opposites attract! Though you have different personalities, patience and understanding can build a strong bond."
            in 21..50 -> "There is a spark between you two! With open communication and spending quality time together, your love can blossom into something beautiful."
            in 51..80 -> "Great match! You share a deep connection and mutual respect. Keep nurturing your relationship and enjoying life together!"
            else -> "A match made in heaven! Your souls resonate on the exact same wavelength. You are truly meant for each other!"
        }

        return NameTestResult(
            yourName = name1,
            partnerName = name2,
            percent = percent,
            description = description
        )
    }
}