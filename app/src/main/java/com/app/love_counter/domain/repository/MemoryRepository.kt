package com.app.love_counter.domain.repository

import com.app.love_counter.domain.model.MemoryInfo
import kotlinx.coroutines.flow.Flow

interface MemoryRepository {
    fun getAllMemories(): Flow<List<MemoryInfo>>
    suspend fun addMemory(memory: MemoryInfo)
    suspend fun deleteMemory(memory: MemoryInfo)
}