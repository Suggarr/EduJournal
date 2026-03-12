package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.LessonTypeDao
import com.edujournal.data.local.database.entities.LessonTypeEntity
import kotlinx.coroutines.flow.Flow

class LessonTypeLocalDataSource(
    private val dao: LessonTypeDao
) {
    fun observeLessonTypes(): Flow<List<LessonTypeEntity>> {
        return dao.observeLessonTypes()
    }

    suspend fun insert(lessonType: LessonTypeEntity) {
        dao.insert(lessonType)
    }
}