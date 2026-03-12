package com.edujournal.domain.repository

import com.edujournal.domain.model.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getGroups(): Flow<List<Group>>
    suspend fun createGroup(group: Group)
}