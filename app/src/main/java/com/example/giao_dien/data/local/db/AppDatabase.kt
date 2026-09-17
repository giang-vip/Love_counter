package com.example.giao_dien.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.giao_dien.data.local.db.dao.CoupleDao
import com.example.giao_dien.data.local.db.dao.MemoryDao
import com.example.giao_dien.data.local.db.dao.PersonDao
import com.example.giao_dien.data.local.db.entity.CoupleEntity
import com.example.giao_dien.data.local.db.entity.MemoryEntity
import com.example.giao_dien.data.local.db.entity.PersonEntity

@Database(entities = [PersonEntity::class, CoupleEntity::class, MemoryEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personDao(): PersonDao
    abstract fun coupleDao(): CoupleDao
    abstract fun memoryDao(): MemoryDao

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