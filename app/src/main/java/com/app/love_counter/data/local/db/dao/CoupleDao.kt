package com.app.love_counter.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.love_counter.data.local.db.entity.CoupleEntity

@Dao
interface CoupleDao {

    @Query("SELECT * FROM couple_table WHERE id = 1 LIMIT 1")
    fun getCouple(): CoupleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(couple: CoupleEntity)
}