package com.edujournal.domain.repository

import com.edujournal.domain.model.Group

interface GroupRepository {
    suspend fun getGroups(): List<Group>
    suspend fun createGroup(group: Group)
}