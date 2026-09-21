package com.app.love_counter.domain.repository

import com.app.love_counter.domain.model.IntroPage
import com.app.love_counter.domain.model.Language

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