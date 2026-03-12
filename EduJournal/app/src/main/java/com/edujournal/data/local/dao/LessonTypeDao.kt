package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.edujournal.data.local.database.entities.LessonTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonTypeDao {

    @Query("SELECT * FROM lesson_types")
    fun observeLessonTypes(): Flow<List<LessonTypeEntity>>

    @Insert
    suspend fun insert(lessonType: LessonTypeEntity)
}