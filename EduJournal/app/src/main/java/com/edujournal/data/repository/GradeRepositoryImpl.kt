package com.edujournal.data.repository

import com.edujournal.data.local.datasource.GradeLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.DisciplineGradeRecord
import com.edujournal.domain.model.Grade
import com.edujournal.domain.model.JournalRow
import com.edujournal.domain.repository.GradeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GradeRepositoryImpl @Inject constructor(
    private val localDataSource: GradeLocalDataSource
) : GradeRepository {
    override fun getGradesForLesson(lessonId: Long): Flow<List<Grade>> {
        return localDataSource
            .getGradesForLesson(lessonId)
            .map{list -> list.map{it.toDomain() }}
    }

    override suspend fun insertGrade(grade: Grade) {
        localDataSource.insertGrade(grade.toEntity())
    }

    override fun getJournal(
        groupId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long
    ): Flow<List<JournalRow>> {
        return localDataSource.getJournal(groupId, subjectLessonTypeId, semesterId)
    }

    override fun observeDisciplineGrades(
        groupId: Long,
        subjectId: Long,
        semesterId: Long
    ): Flow<List<DisciplineGradeRecord>> {
        return localDataSource.observeDisciplineGrades(groupId, subjectId, semesterId)
    }
}

