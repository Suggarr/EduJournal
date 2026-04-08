package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edujournal.data.local.database.entities.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Query("SELECT * FROM 'groups'")
    fun getGroups(): Flow<List<GroupEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroup(group: GroupEntity)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateGroup(group: GroupEntity)

    @Query("DELETE FROM 'groups' WHERE id = :id")
    suspend fun deleteGroup(id: Long)
}
