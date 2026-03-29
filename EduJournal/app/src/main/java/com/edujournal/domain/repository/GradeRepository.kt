package com.edujournal.domain.repository

import com.edujournal.domain.model.Grade
import com.edujournal.domain.model.JournalRow
import kotlinx.coroutines.flow.Flow

interface GradeRepository {
    fun getGradesForLesson(lessonId: Long): Flow<List<Grade>>

    suspend fun insertGrade(grade: Grade)

    fun getJournal(
        groupId: Long,
        subjectId: Long,
        lessonTypeId: Long
    ): Flow<List<JournalRow>>
}
