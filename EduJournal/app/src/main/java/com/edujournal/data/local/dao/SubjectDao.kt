package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.edujournal.data.local.database.entities.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects")
    fun observeSubjects(): Flow<List<SubjectEntity>>

    @Insert
    suspend fun insert(subject: SubjectEntity)
}