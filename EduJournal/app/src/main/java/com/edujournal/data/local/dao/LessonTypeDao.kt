package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edujournal.data.local.database.entities.LessonTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonTypeDao {

    @Query("SELECT * FROM lesson_types")
    fun observeLessonTypes(): Flow<List<LessonTypeEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(lessonType: LessonTypeEntity)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(lessonType: LessonTypeEntity)

    @Query("DELETE FROM lesson_types WHERE id = :typeId")
    suspend fun deleteById(typeId: Long)
}
