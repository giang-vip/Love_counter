package com.example.giao_dien.data.repository

import android.content.Context
import com.example.giao_dien.data.local.AppPreferences
import com.example.giao_dien.data.local.db.AppDatabase
import com.example.giao_dien.data.local.db.entity.CoupleEntity
import com.example.giao_dien.data.local.db.entity.PersonEntity
import com.example.giao_dien.domain.model.CoupleInfo
import com.example.giao_dien.domain.model.GenderType
import com.example.giao_dien.domain.model.PersonInfo
import com.example.giao_dien.domain.repository.CoupleRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class CoupleRepositoryImpl(
    context: Context,
    private val appPreferences: AppPreferences = AppPreferences(context)
) : CoupleRepository {

    private val db = AppDatabase.getDatabase(context)
    private val personDao = db.personDao()
    private val coupleDao = db.coupleDao()

    override fun getCoupleInfo(): CoupleInfo {
        val male = getPersonInfo(isMale = true)
        val female = getPersonInfo(isMale = false)
        val startDate = getLoveStartDate()
        return CoupleInfo(male, female, startDate)
    }

    override fun getPersonInfo(isMale: Boolean): PersonInfo {
        val entity = try {
            personDao.getPerson(isMale)
        } catch (e: Exception) {
            null
        }
        return if (entity != null) {
            val gender = try {
                GenderType.valueOf(entity.gender)
            } catch (e: Exception) {
                if (isMale) GenderType.MALE else GenderType.FEMALE
            }
            PersonInfo(entity.name, gender, entity.birthday, entity.avatarUri)
        } else {
            val prefPerson = appPreferences.getPersonInfo(isMale)
            try {
                savePersonInfo(isMale, prefPerson)
            } catch (e: Exception) {
                // Ignore DB fallback
            }
            prefPerson
        }
    }

    override fun savePersonInfo(isMale: Boolean, personInfo: PersonInfo) {
        try {
            val entity = PersonEntity(
                isMale = isMale,
                name = personInfo.name,
                gender = personInfo.gender.name,
                birthday = personInfo.birthday,
                avatarUri = personInfo.avatarUri
            )
            personDao.insertOrUpdate(entity)
        } catch (e: Exception) {
            // DB fallback
        }
        appPreferences.savePersonInfo(isMale, personInfo)
    }

    override fun getLoveDaysCount(): Int {
        val startDateStr = getLoveStartDate()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val startDate = sdf.parse(startDateStr) ?: Date()
            val today = Date()
            val diffInMillis = today.time - startDate.time
            val days = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
            if (days < 0) 0 else days
        } catch (e: Exception) {
            0
        }
    }

    override fun getLoveStartDate(): String {
        val entity = try {
            coupleDao.getCouple()
        } catch (e: Exception) {
            null
        }
        return entity?.loveStartDate ?: appPreferences.getLoveStartDate() ?: "11/09/2023"
    }

    override fun saveLoveStartDate(startDate: String) {
        try {
            val existing = coupleDao.getCouple()
            val updated = CoupleEntity(
                id = 1,
                loveStartDate = startDate,
                backgroundUri = existing?.backgroundUri ?: appPreferences.getBackgroundUri()
            )
            coupleDao.insertOrUpdate(updated)
        } catch (e: Exception) {
            // DB fallback
        }
        appPreferences.saveLoveStartDate(startDate)
    }

    override fun getBackgroundUri(): String? {
        val entity = try {
            coupleDao.getCouple()
        } catch (e: Exception) {
            null
        }
        return entity?.backgroundUri ?: appPreferences.getBackgroundUri()
    }

    override fun saveBackgroundUri(uri: String) {
        try {
            val existing = coupleDao.getCouple()
            val updated = CoupleEntity(
                id = 1,
                loveStartDate = existing?.loveStartDate ?: appPreferences.getLoveStartDate(),
                backgroundUri = uri
            )
            coupleDao.insertOrUpdate(updated)
        } catch (e: Exception) {
            // DB fallback
        }
        appPreferences.saveBackgroundUri(uri)
    }
}