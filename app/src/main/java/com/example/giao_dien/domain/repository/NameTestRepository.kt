package com.example.giao_dien.domain.repository

import com.example.giao_dien.domain.model.NameTestResult

interface NameTestRepository {
    fun calculateNameTestResult(yourName: String, partnerName: String): NameTestResult
}