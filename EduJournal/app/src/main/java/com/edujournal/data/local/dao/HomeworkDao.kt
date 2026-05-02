package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edujournal.data.local.database.entities.HomeworkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeworkDao {

    @Query(
        """
        SELECT * FROM homeworks
        WHERE lessonId = :lessonId
        LIMIT 1
        """
    )
    fun observeHomework(lessonId: Long): Flow<HomeworkEntity?>

    @Query("SELECT lessonId FROM homeworks WHERE lessonId IN (:lessonIds)")
    fun observeHomeworkLessonIds(lessonIds: List<Long>): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(homework: HomeworkEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(homework: HomeworkEntity): Int

    @Query("DELETE FROM homeworks WHERE id = :id")
    suspend fun deleteById(id: Long)
}
