package com.edujournal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edujournal.data.local.database.entities.TopicTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicTemplateDao {

    @Query(
        """
        SELECT * FROM topic_templates
        WHERE semesterId = :semesterId AND subjectLessonTypeId = :subjectLessonTypeId
        ORDER BY orderInType
        """
    )
    fun observeByContext(semesterId: Long, subjectLessonTypeId: Long): Flow<List<TopicTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: TopicTemplateEntity): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(item: TopicTemplateEntity): Int

    @Query("DELETE FROM topic_templates WHERE id = :id")
    suspend fun deleteById(id: Long)
}


