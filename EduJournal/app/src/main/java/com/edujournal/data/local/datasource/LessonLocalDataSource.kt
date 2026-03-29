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
        subjectId: Long,
        lessonTypeId: Long
    ): Flow<List<LessonEntity>> {
        return lessonDao.observeLessons(groupId, subjectId, lessonTypeId)
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

