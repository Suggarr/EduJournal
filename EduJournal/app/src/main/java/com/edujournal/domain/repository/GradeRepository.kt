package com.edujournal.domain.repository

import com.edujournal.domain.model.Grade
import com.edujournal.domain.model.DisciplineGradeRecord
import com.edujournal.domain.model.JournalRow
import kotlinx.coroutines.flow.Flow

interface GradeRepository {
    fun getGradesForLesson(lessonId: Long): Flow<List<Grade>>

    suspend fun insertGrade(grade: Grade)

    fun getJournal(
        groupId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long
    ): Flow<List<JournalRow>>

    fun observeDisciplineGrades(
        groupId: Long,
        subjectId: Long,
        semesterId: Long
    ): Flow<List<DisciplineGradeRecord>>
}

