package com.example.giao_dien.domain.repository

import com.example.giao_dien.domain.model.CoupleInfo
import com.example.giao_dien.domain.model.PersonInfo

interface CoupleRepository {
    fun getCoupleInfo(): CoupleInfo
    fun getPersonInfo(isMale: Boolean): PersonInfo
    fun savePersonInfo(isMale: Boolean, personInfo: PersonInfo)
    fun getLoveDaysCount(): Int
    fun getLoveStartDate(): String
    fun saveLoveStartDate(startDate: String)
    fun getBackgroundUri(): String?
    fun saveBackgroundUri(uri: String)
}