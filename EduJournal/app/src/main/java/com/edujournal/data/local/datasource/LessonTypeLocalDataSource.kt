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

    suspend fun insertLessonType(lessonType: LessonTypeEntity): Long {
        return lessonTypeDao.insert(lessonType)
    }

    suspend fun updateLessonType(lessonType: LessonTypeEntity): Int {
        return lessonTypeDao.update(lessonType)
    }

    suspend fun existsById(id: Long): Boolean {
        return lessonTypeDao.existsById(id)
    }

    suspend fun existsByName(name: String): Boolean {
        return lessonTypeDao.existsByName(name)
    }

    suspend fun existsByNameExceptId(name: String, id: Long): Boolean {
        return lessonTypeDao.existsByNameExceptId(name, id)
    }

    suspend fun deleteLessonType(typeId: Long) {
        lessonTypeDao.deleteById(typeId)
    }
}
