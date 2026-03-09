package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.edujournal.data.local.database.entities.GroupEntity

@Dao
interface GroupDao {

    @Query("SELECT * FROM 'groups'")
    suspend fun getAll(): List<GroupEntity>

    @Insert
    suspend fun insert(group: GroupEntity)
}