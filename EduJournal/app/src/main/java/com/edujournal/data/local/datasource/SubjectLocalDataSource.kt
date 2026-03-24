package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.SubjectDao
import com.edujournal.data.local.database.entities.SubjectEntity
import kotlinx.coroutines.flow.Flow

class SubjectLocalDataSource(
    private val subjectDao: SubjectDao
) {
    fun observeSubjects(): Flow<List<SubjectEntity>> {
        return subjectDao.observeSubjects()
    }

    suspend fun insertSubject(subject: SubjectEntity) {
        subjectDao.insert(subject)
    }

    suspend fun updateSubject(subject: SubjectEntity) {
        subjectDao.update(subject)
    }

    suspend fun deleteSubject(subjectId: Long) {
        subjectDao.deleteById(subjectId)
    }
}