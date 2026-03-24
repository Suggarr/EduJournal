package com.edujournal.data.repository

import com.edujournal.data.local.datasource.GroupLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.Group
import com.edujournal.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GroupRepositoryImpl (
    private val localDataSource: GroupLocalDataSource
): GroupRepository {

    override fun getGroups(): Flow<List<Group>>{
        return localDataSource
            .getAllGroups()
            .map{ list -> list.map { it.toDomain() }}
    }

    override suspend fun createGroup(group: Group){
        localDataSource.insertGroup(
            group.toEntity()
        )
    }

    override suspend fun updateGroup(group: Group) {
        localDataSource.updateGroup(group.toEntity())
    }

    override suspend fun deleteGroup(id: Long) {
        localDataSource.deleteGroup(id)
    }
}