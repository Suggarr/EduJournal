package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.SubjectLessonTypeHoursDao
import com.edujournal.data.local.database.entities.SubjectLessonTypeHoursEntity
import kotlinx.coroutines.flow.Flow

class SubjectLessonTypeHoursLocalDataSource(
    private val subjectLessonTypeHoursDao: SubjectLessonTypeHoursDao
) {
    fun observeAll(): Flow<List<SubjectLessonTypeHoursEntity>> {
        return subjectLessonTypeHoursDao.observeAll()
    }

    suspend fun getBySubjectId(subjectId: Long): List<SubjectLessonTypeHoursEntity> {
        return subjectLessonTypeHoursDao.getBySubjectId(subjectId)
    }

    suspend fun replaceForSubject(
        subjectId: Long,
        items: List<SubjectLessonTypeHoursEntity>
    ) {
        subjectLessonTypeHoursDao.replaceForSubject(subjectId, items)
    }
}
