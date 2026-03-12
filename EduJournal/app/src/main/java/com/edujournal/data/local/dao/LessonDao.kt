package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.edujournal.data.local.database.entities.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Query("SELECT * FROM lessons")
    fun getLessons(): Flow<List<LessonEntity>>

    @Insert
    suspend fun insertLesson(lesson: LessonEntity)
}