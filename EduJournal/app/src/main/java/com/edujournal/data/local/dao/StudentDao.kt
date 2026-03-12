package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.edujournal.data.local.database.entities.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students Where groupId = :groupId")
    fun observeStudents(groupId: Long): Flow<List<StudentEntity>>

    @Insert
    suspend fun insert(student: StudentEntity)
}