package com.app.love_counter.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.love_counter.data.local.db.entity.PersonEntity

@Dao
interface PersonDao {

    @Query("SELECT * FROM person_table WHERE isMale = :isMale LIMIT 1")
    fun getPerson(isMale: Boolean): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(person: PersonEntity)
}