package com.example.giao_dien.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.giao_dien.data.local.db.entity.PersonEntity

@Dao
interface PersonDao {

    @Query("SELECT * FROM person_table WHERE isMale = :isMale LIMIT 1")
    fun getPerson(isMale: Boolean): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(person: PersonEntity)
}