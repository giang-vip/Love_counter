package com.app.love_counter.data.repository

import android.content.Context
import com.app.love_counter.data.local.db.AppDatabase
import com.app.love_counter.data.local.db.entity.CoupleEntity
import com.app.love_counter.data.local.db.entity.PersonEntity
import com.app.love_counter.domain.model.CoupleInfo
import com.app.love_counter.domain.model.GenderType
import com.app.love_counter.domain.model.PersonInfo
import com.app.love_counter.domain.repository.CoupleRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * REPOSITORY QUẢN LÝ THÔNG TIN CẶP ĐÔI (DÙNG ROOM DATABASE TRỰC TIẾP)
 * Không sử dụng SharedPreferences (AppPreferences) để lưu dữ liệu quan trọng như
 * tên tuổi, ngày kỷ niệm, hình nền nhằm tránh sự trùng lặp và đảm bảo bảo mật.
 */
class CoupleRepositoryImpl(
    context: Context
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
            // Nếu chưa có trong DB, trả về thông tin mặc định theo chuẩn dự án
            val defaultName = if (isMale) "Sasuke" else "Naruto"
            val defaultGender = if (isMale) GenderType.MALE else GenderType.FEMALE
            val defaultBirthday = "22/02/1997"
            
            val defaultPerson = PersonInfo(defaultName, defaultGender, defaultBirthday, null)
            try {
                savePersonInfo(isMale, defaultPerson)
            } catch (e: Exception) {
                // Bỏ qua lỗi fallback
            }
            defaultPerson
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
            e.printStackTrace()
        }
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
        return entity?.loveStartDate ?: "11/09/2023"
    }

    override fun saveLoveStartDate(startDate: String) {
        try {
            val existing = coupleDao.getCouple()
            val updated = CoupleEntity(
                id = 1,
                loveStartDate = startDate,
                backgroundUri = existing?.backgroundUri
            )
            coupleDao.insertOrUpdate(updated)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getBackgroundUri(): String? {
        val entity = try {
            coupleDao.getCouple()
        } catch (e: Exception) {
            null
        }
        return entity?.backgroundUri
    }

    override fun saveBackgroundUri(uri: String) {
        try {
            val existing = coupleDao.getCouple()
            val updated = CoupleEntity(
                id = 1,
                loveStartDate = existing?.loveStartDate ?: "11/09/2023",
                backgroundUri = uri
            )
            coupleDao.insertOrUpdate(updated)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
