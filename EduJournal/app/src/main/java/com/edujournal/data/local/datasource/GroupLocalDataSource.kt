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

    suspend fun existsById(id: Long): Boolean {
        return groupDao.existsById(id)
    }

    suspend fun existsByName(name: String): Boolean {
        return groupDao.existsByName(name)
    }

    suspend fun existsByNameExceptId(name: String, id: Long): Boolean {
        return groupDao.existsByNameExceptId(name, id)
    }

    suspend fun deleteGroup(id: Long) {
        groupDao.deleteGroup(id)
    }
}


