package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.LessonTypeDao
import com.edujournal.data.local.database.entities.LessonTypeEntity
import kotlinx.coroutines.flow.Flow

class LessonTypeLocalDataSource(
    private val lessonTypeDao: LessonTypeDao
) {
    fun observeLessonTypes(): Flow<List<LessonTypeEntity>> {
        return lessonTypeDao.observeLessonTypes()
    }

    suspend fun insertLessonType(lessonType: LessonTypeEntity) {
        lessonTypeDao.insert(lessonType)
    }

    suspend fun updateLessonType(lessonType: LessonTypeEntity) {
        lessonTypeDao.update(lessonType)
    }

    suspend fun deleteLessonType(typeId: Long) {
        lessonTypeDao.deleteById(typeId)
    }
}