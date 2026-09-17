package com.example.giao_dien.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.giao_dien.domain.model.GenderType
import com.example.giao_dien.domain.model.PersonInfo

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getSelectedLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun saveSelectedLanguage(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun isIntroCompleted(): Boolean {
        return prefs.getBoolean(KEY_INTRO_COMPLETED, false)
    }

    fun setIntroCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_INTRO_COMPLETED, completed).apply()
    }

    fun isPermissionGranted(): Boolean {
        return prefs.getBoolean(KEY_PERMISSION_GRANTED, false)
    }

    fun setPermissionGranted(granted: Boolean) {
        prefs.edit().putBoolean(KEY_PERMISSION_GRANTED, granted).apply()
    }

    // Person Info
    fun getPersonInfo(isMale: Boolean): PersonInfo {
        val prefix = if (isMale) "male_" else "female_"
        val defaultName = if (isMale) "Sasuke" else "Naruto"
        val defaultGender = if (isMale) GenderType.MALE else GenderType.FEMALE
        val defaultBirthday = "22/02/1997"

        val name = prefs.getString("${prefix}name", defaultName) ?: defaultName
        val genderStr = prefs.getString("${prefix}gender", defaultGender.name) ?: defaultGender.name
        val gender = try { GenderType.valueOf(genderStr) } catch (e: Exception) { defaultGender }
        val birthday = prefs.getString("${prefix}birthday", defaultBirthday) ?: defaultBirthday
        val avatarUri = prefs.getString("${prefix}avatar_uri", null)

        return PersonInfo(name, gender, birthday, avatarUri)
    }

    fun savePersonInfo(isMale: Boolean, personInfo: PersonInfo) {
        val prefix = if (isMale) "male_" else "female_"
        prefs.edit()
            .putString("${prefix}name", personInfo.name)
            .putString("${prefix}gender", personInfo.gender.name)
            .putString("${prefix}birthday", personInfo.birthday)
            .putString("${prefix}avatar_uri", personInfo.avatarUri)
            .apply()
    }

    fun getLoveStartDate(): String? {
        return prefs.getString(KEY_LOVE_START_DATE, null)
    }

    fun saveLoveStartDate(date: String) {
        prefs.edit().putString(KEY_LOVE_START_DATE, date).apply()
    }

    fun getBackgroundUri(): String? {
        return prefs.getString(KEY_BACKGROUND_URI, null)
    }

    fun saveBackgroundUri(uri: String) {
        prefs.edit().putString(KEY_BACKGROUND_URI, uri).apply()
    }

    companion object {
        private const val PREF_NAME = "app_preferences"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_INTRO_COMPLETED = "key_intro_completed"
        private const val KEY_PERMISSION_GRANTED = "key_permission_granted"
        private const val KEY_LOVE_START_DATE = "key_love_start_date"
        private const val KEY_BACKGROUND_URI = "key_background_uri"
    }
}