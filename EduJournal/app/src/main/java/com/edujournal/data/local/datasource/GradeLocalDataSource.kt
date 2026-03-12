package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.GradeDao
import com.edujournal.data.local.database.entities.GradeEntity
import com.edujournal.domain.model.JournalRow
import com.edujournal.domain.repository.GroupRepository
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

    fun getJournal(groupId: Long): Flow<List<JournalRow>> {
        return gradeDao.getJournal(groupId)
    }
}