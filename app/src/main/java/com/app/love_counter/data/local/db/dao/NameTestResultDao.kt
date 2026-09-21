package com.app.love_counter.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.love_counter.data.local.db.entity.NameTestResultEntity

@Dao
interface NameTestResultDao {
    @Insert
    fun insertResult(result: NameTestResultEntity)

    @Query("SELECT * FROM name_test_results ORDER BY timestamp DESC")
    fun getAllResults(): List<NameTestResultEntity>
}
