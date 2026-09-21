package com.app.love_counter.data.repository

import com.app.love_counter.R
import com.app.love_counter.data.local.AppPreferences
import com.app.love_counter.domain.model.IntroPage
import com.app.love_counter.domain.model.Language
import com.app.love_counter.domain.repository.AppRepository

class AppRepositoryImpl(
    private val appPreferences: AppPreferences
) : AppRepository {

    override fun getLanguages(): List<Language> {
        return listOf(
            Language(
                code = "en",
                nameRes = R.string.language_english,
                flagRes = R.drawable.flag_en
            ),
            Language(
                code = "hi",
                nameRes = R.string.language_hindi,
                flagRes = R.drawable.flag_hindi
            ),
            Language(
                code = "es",
                nameRes = R.string.language_spanish,
                flagRes = R.drawable.flag_spain
            ),
            Language(
                code = "fr",
                nameRes = R.string.language_french,
                flagRes = R.drawable.flag_fra
            ),
            Language(
                code = "pt",
                nameRes = R.string.language_portuguese,
                flagRes = R.drawable.flag_port
            ),
            Language(
                code = "id",
                nameRes = R.string.language_indonesian,
                flagRes = R.drawable.flag_indonesia
            ),
            Language(
                code = "de",
                nameRes = R.string.language_german,
                flagRes = R.drawable.flag_ger
            )
        )
    }

    override fun getSelectedLanguage(): String {
        return appPreferences.getSelectedLanguage()
    }

    override fun saveSelectedLanguage(languageCode: String) {
        appPreferences.saveSelectedLanguage(languageCode)
    }

    override fun getIntroPages(): List<IntroPage> {
        return listOf(
            IntroPage(
                imageRes = R.drawable.img_intro1,
                titleRes = R.string.intro_title_1,
                descriptionRes = R.string.intro_description_1
            ),
            IntroPage(
                imageRes = R.drawable.img_intro2,
                titleRes = R.string.intro_title_2,
                descriptionRes = R.string.intro_description_2
            ),
            IntroPage(
                imageRes = R.drawable.img_intro3,
                titleRes = R.string.intro_title_3,
                descriptionRes = R.string.intro_description_3
            )
        )
    }

    override fun isIntroCompleted(): Boolean {
        return appPreferences.isIntroCompleted()
    }

    override fun setIntroCompleted(completed: Boolean) {
        appPreferences.setIntroCompleted(completed)
    }

    override fun isPermissionGranted(): Boolean {
        return appPreferences.isPermissionGranted()
    }

    override fun setPermissionGranted(granted: Boolean) {
        appPreferences.setPermissionGranted(granted)
    }
}