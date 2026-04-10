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
    suspend fun insertGroup(group: GroupEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateGroup(group: GroupEntity): Int

    @Query("SELECT EXISTS(SELECT 1 FROM 'groups' WHERE id = :id)")
    suspend fun existsById(id: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM 'groups' WHERE name = :name)")
    suspend fun existsByName(name: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM 'groups' WHERE name = :name AND id != :id)")
    suspend fun existsByNameExceptId(name: String, id: Long): Boolean

    @Query("DELETE FROM 'groups' WHERE id = :id")
    suspend fun deleteGroup(id: Long)
}
