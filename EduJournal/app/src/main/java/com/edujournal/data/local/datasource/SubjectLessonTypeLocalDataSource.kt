package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.SubjectLessonTypeDao
import com.edujournal.data.local.database.entities.SubjectLessonTypeEntity
import kotlinx.coroutines.flow.Flow

class SubjectLessonTypeLocalDataSource(
    private val lessonTypeDao: SubjectLessonTypeDao
) {
    fun observeLessonTypes(subjectId: Long): Flow<List<SubjectLessonTypeEntity>> {
        return lessonTypeDao.observeLessonTypes(subjectId)
    }

    suspend fun getById(id: Long): SubjectLessonTypeEntity? {
        return lessonTypeDao.getById(id)
    }

    fun observeById(id: Long): Flow<SubjectLessonTypeEntity?> {
        return lessonTypeDao.observeById(id)
    }

    suspend fun insertLessonType(SubjectLessonType: SubjectLessonTypeEntity): Long {
        return lessonTypeDao.insert(SubjectLessonType)
    }

    suspend fun updateLessonType(SubjectLessonType: SubjectLessonTypeEntity): Int {
        return lessonTypeDao.update(SubjectLessonType)
    }

    suspend fun existsById(id: Long): Boolean {
        return lessonTypeDao.existsById(id)
    }

    suspend fun existsByName(subjectId: Long, name: String): Boolean {
        return lessonTypeDao.existsByName(subjectId, name)
    }

    suspend fun existsByNameExceptId(subjectId: Long, name: String, id: Long): Boolean {
        return lessonTypeDao.existsByNameExceptId(subjectId, name, id)
    }

    suspend fun deleteLessonType(typeId: Long) {
        lessonTypeDao.deleteById(typeId)
    }
}


