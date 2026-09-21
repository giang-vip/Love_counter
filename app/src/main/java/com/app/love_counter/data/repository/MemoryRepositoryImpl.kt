package com.app.love_counter.data.repository

import android.content.Context
import com.app.love_counter.data.local.db.AppDatabase
import com.app.love_counter.data.local.db.entity.MemoryEntity
import com.app.love_counter.domain.model.MemoryInfo
import com.app.love_counter.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MemoryRepositoryImpl(
    context: Context
) : MemoryRepository {

    private val memoryDao = AppDatabase.getDatabase(context).memoryDao()

    override fun getAllMemories(): Flow<List<MemoryInfo>> {
        return memoryDao.getAllMemories().map { entities ->
            entities.map { entity ->
                MemoryInfo(
                    id = entity.id,
                    title = entity.title,
                    date = entity.date,
                    imageUri = entity.imageUri
                )
            }
        }
    }

    override suspend fun addMemory(memory: MemoryInfo) {
        val entity = MemoryEntity(
            id = memory.id,
            title = memory.title,
            date = memory.date,
            imageUri = memory.imageUri
        )
        memoryDao.insertMemory(entity)
    }

    override suspend fun deleteMemory(memory: MemoryInfo) {
        val entity = MemoryEntity(
            id = memory.id,
            title = memory.title,
            date = memory.date,
            imageUri = memory.imageUri
        )
        memoryDao.deleteMemory(entity)
    }
}