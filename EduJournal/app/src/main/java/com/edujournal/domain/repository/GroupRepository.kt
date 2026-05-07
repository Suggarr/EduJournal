package com.edujournal.domain.repository

import com.edujournal.domain.model.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getGroups(): Flow<List<Group>>
    suspend fun createGroup(group: Group): Long

    suspend fun updateGroup(group: Group): Int
    suspend fun deleteGroup(id: Long)
}


