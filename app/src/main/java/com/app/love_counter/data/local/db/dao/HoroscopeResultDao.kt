package com.app.love_counter.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.love_counter.data.local.db.entity.HoroscopeResultEntity

@Dao
interface HoroscopeResultDao {
    @Insert
    fun insertResult(result: HoroscopeResultEntity)

    @Query("SELECT * FROM horoscope_results ORDER BY timestamp DESC")
    fun getAllResults(): List<HoroscopeResultEntity>
}
