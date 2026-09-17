package com.example.giao_dien.domain.repository

import com.example.giao_dien.domain.model.MemoryInfo
import kotlinx.coroutines.flow.Flow

interface MemoryRepository {
    fun getAllMemories(): Flow<List<MemoryInfo>>
    suspend fun addMemory(memory: MemoryInfo)
    suspend fun deleteMemory(memory: MemoryInfo)
}