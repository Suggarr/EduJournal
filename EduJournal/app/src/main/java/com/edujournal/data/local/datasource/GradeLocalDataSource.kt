package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.GradeDao
import com.edujournal.data.local.database.entities.GradeEntity
import com.edujournal.domain.model.DisciplineGradeRecord
import com.edujournal.domain.model.JournalRow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GradeLocalDataSource @Inject constructor(
    private val gradeDao: GradeDao
) {
    fun getGradesForLesson(lessonId: Long): Flow<List<GradeEntity>> {
        return gradeDao.getGradesForLesson(lessonId)
    }

    suspend fun insertGrade(grade: GradeEntity){
        gradeDao.insertGrade(grade)
    }

    fun getJournal(
        groupId: Long,
        subjectId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long
    ): Flow<List<JournalRow>> {
        return gradeDao.getJournal(groupId, subjectId, subjectLessonTypeId, semesterId)
    }

    fun observeDisciplineGrades(
        groupId: Long,
        subjectId: Long,
        semesterId: Long
    ): Flow<List<DisciplineGradeRecord>> {
        return gradeDao.observeDisciplineGrades(groupId, subjectId, semesterId)
    }
}

