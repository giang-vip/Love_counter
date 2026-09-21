package com.app.love_counter.domain.repository

import com.app.love_counter.domain.model.NameTestResult

interface NameTestRepository {
    fun calculateNameTestResult(yourName: String, partnerName: String): NameTestResult
}