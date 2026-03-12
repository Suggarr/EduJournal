package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.LessonDao
import com.edujournal.data.local.database.entities.LessonEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LessonLocalDataSource @Inject constructor(
    private val lessonDao: LessonDao
){
    fun getLessons(): Flow<List<LessonEntity>> {
        return lessonDao.getLessons()
    }

    suspend fun insertLesson(lesson: LessonEntity){
        lessonDao.insertLesson(lesson)
    }
}

