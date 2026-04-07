package com.edujournal.data.repository

import com.edujournal.data.local.datasource.LessonLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.Lesson
import com.edujournal.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor(
    private val lessonLocalDataSource: LessonLocalDataSource
) : LessonRepository {

    override fun observeLessons(
        groupId: Long,
        subjectId: Long,
        lessonTypeId: Long,
        semesterId: Long
    ): Flow<List<Lesson>> {
        return lessonLocalDataSource
            .observeLessons(groupId, subjectId, lessonTypeId, semesterId)
            .map { list -> list.map { it.toDomain() }
        }

    }

    override suspend fun insertLesson(lesson: Lesson) {
        lessonLocalDataSource.insertLesson(lesson.toEntity())
    }

    override suspend fun updateLesson(lesson: Lesson) {
        lessonLocalDataSource.updateLesson(lesson.toEntity())
    }

    override suspend fun deleteLesson(lessonId: Long) {
        lessonLocalDataSource.deleteLesson(lessonId)
    }
}
