package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.GroupDao
import com.edujournal.data.local.database.entities.GroupEntity
import kotlinx.coroutines.flow.Flow

class GroupLocalDataSource(
    private val groupDao: GroupDao
)
{
    fun getAllGroups(): Flow<List<GroupEntity>>{
        return groupDao.getGroups()
    }

    suspend fun insertGroup(group: GroupEntity): Long{
        return groupDao.insertGroup(group)
    }

    suspend fun updateGroup(group: GroupEntity): Int{
        return groupDao.updateGroup(group)
    }

    suspend fun deleteGroup(id: Long) {
        groupDao.deleteGroup(id)
    }
}


