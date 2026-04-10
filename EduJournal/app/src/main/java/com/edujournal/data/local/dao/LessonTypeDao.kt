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
    suspend fun insert(lessonType: LessonTypeEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(lessonType: LessonTypeEntity): Int

    @Query("SELECT EXISTS(SELECT 1 FROM lesson_types WHERE id = :id)")
    suspend fun existsById(id: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM lesson_types WHERE name = :name)")
    suspend fun existsByName(name: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM lesson_types WHERE name = :name AND id != :id)")
    suspend fun existsByNameExceptId(name: String, id: Long): Boolean

    @Query("DELETE FROM lesson_types WHERE id = :typeId")
    suspend fun deleteById(typeId: Long)
}
