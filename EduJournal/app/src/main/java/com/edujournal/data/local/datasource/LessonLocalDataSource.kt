package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.LessonDao
import com.edujournal.data.local.database.entities.LessonEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LessonLocalDataSource @Inject constructor(
    private val lessonDao: LessonDao
){
    fun observeLessons(
        groupId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long
    ): Flow<List<LessonEntity>> {
        return lessonDao.observeLessons(groupId, subjectLessonTypeId, semesterId)
    }

    fun observeLessonById(lessonId: Long): Flow<LessonEntity?> {
        return lessonDao.observeLessonById(lessonId)
    }

    suspend fun insertLesson(lesson: LessonEntity) {
        lessonDao.insertLesson(lesson)
    }

    suspend fun updateLesson(lesson: LessonEntity) {
        lessonDao.updateLesson(lesson)
    }

    suspend fun deleteLesson(lessonId: Long) {
        lessonDao.deleteLesson(lessonId)
    }
}


