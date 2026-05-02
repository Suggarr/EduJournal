package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.edujournal.data.local.database.entities.HomeworkSubmissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeworkSubmissionDao {

    @Query("SELECT * FROM homework_submissions WHERE homeworkId = :homeworkId ORDER BY id ASC")
    fun observeSubmissions(homeworkId: Long): Flow<List<HomeworkSubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(submission: HomeworkSubmissionEntity)
}

