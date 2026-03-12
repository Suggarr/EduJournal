package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.GroupDao
import com.edujournal.data.local.database.entities.GroupEntity
import kotlinx.coroutines.flow.Flow

class GroupLocalDataSource(
    private val groupDao: GroupDao
)
{
    fun getAllGroups(): Flow<List<GroupEntity>>{
        return groupDao.getAll()
    }

    suspend fun insertGroup(group: GroupEntity){
        groupDao.insert(group)
    }
}