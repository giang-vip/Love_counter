package com.app.love_counter.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.love_counter.data.local.db.dao.CoupleDao
import com.app.love_counter.data.local.db.dao.MemoryDao
import com.app.love_counter.data.local.db.dao.PersonDao
import com.app.love_counter.data.local.db.dao.HoroscopeResultDao
import com.app.love_counter.data.local.db.dao.NameTestResultDao
import com.app.love_counter.data.local.db.entity.CoupleEntity
import com.app.love_counter.data.local.db.entity.MemoryEntity
import com.app.love_counter.data.local.db.entity.PersonEntity
import com.app.love_counter.data.local.db.entity.HoroscopeResultEntity
import com.app.love_counter.data.local.db.entity.NameTestResultEntity

@Database(
    entities = [
        PersonEntity::class, 
        CoupleEntity::class, 
        MemoryEntity::class, 
        HoroscopeResultEntity::class, 
        NameTestResultEntity::class
    ], 
    version = 3, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personDao(): PersonDao
    abstract fun coupleDao(): CoupleDao
    abstract fun memoryDao(): MemoryDao
    abstract fun horoscopeResultDao(): HoroscopeResultDao
    abstract fun nameTestResultDao(): NameTestResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "love_counter_db"
                ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
