package com.example.giao_dien.domain.repository

import com.example.giao_dien.domain.model.IntroPage
import com.example.giao_dien.domain.model.Language

interface AppRepository {
    fun getLanguages(): List<Language>
    fun getSelectedLanguage(): String
    fun saveSelectedLanguage(languageCode: String)
    fun getIntroPages(): List<IntroPage>
    fun isIntroCompleted(): Boolean
    fun setIntroCompleted(completed: Boolean)
    fun isPermissionGranted(): Boolean
    fun setPermissionGranted(granted: Boolean)
}