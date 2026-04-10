package com.edujournal.domain.repository

import com.edujournal.domain.model.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getGroups(): Flow<List<Group>>
    suspend fun createGroup(group: Group): Long

    suspend fun updateGroup(group: Group): Int
    suspend fun existsById(id: Long): Boolean
    suspend fun existsByName(name: String): Boolean
    suspend fun existsByNameExceptId(name: String, id: Long): Boolean
    suspend fun deleteGroup(id: Long)
}
