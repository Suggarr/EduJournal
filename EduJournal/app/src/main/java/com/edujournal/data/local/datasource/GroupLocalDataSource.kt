package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.GroupDao
import com.edujournal.data.local.database.entities.GroupEntity

class GroupLocalDataSource(
    private val groupDao: GroupDao
)
{
    suspend fun getAllGroups(): List<GroupEntity>{
        return groupDao.getAll()
    }

    suspend fun insertGroup(group: GroupEntity){
        groupDao.insert(group)
    }
}