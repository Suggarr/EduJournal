package com.edujournal.data.repository

import com.edujournal.data.local.datasource.GroupLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.Group
import com.edujournal.domain.repository.GroupRepository

class GroupRepositoryImpl (
    private val localDataSource: GroupLocalDataSource
): GroupRepository {

    override suspend fun getGroups(): List<Group>{
        return localDataSource
            .getAllGroups()
            .map{ entity -> entity.toDomain()}
    }

    override suspend fun createGroup(group: Group){
        localDataSource.insertGroup(
            group.toEntity()
        )
    }
}