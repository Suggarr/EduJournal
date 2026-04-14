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

    fun observeSubjectsBySemester(semesterId: Long): Flow<List<SubjectEntity>> {
        return subjectDao.observeSubjectsBySemester(semesterId)
    }

    fun observeSemesterIdsBySubject(subjectId: Long): Flow<List<Long>> {
        return subjectDao.observeSemesterIdsBySubject(subjectId)
    }

    suspend fun insertSubject(subject: SubjectEntity): Long {
        return subjectDao.insert(subject)
    }

    suspend fun updateSubject(subject: SubjectEntity): Int {
        return subjectDao.update(subject)
    }

    suspend fun existsById(id: Long): Boolean {
        return subjectDao.existsById(id)
    }

    suspend fun existsByName(name: String): Boolean {
        return subjectDao.existsByName(name)
    }

    suspend fun existsByNameExceptId(name: String, id: Long): Boolean {
        return subjectDao.existsByNameExceptId(name, id)
    }

    suspend fun replaceSemestersBySubjectId(subjectId: Long, semesterIds: List<Long>) {
        subjectDao.replaceSemestersBySubjectId(subjectId, semesterIds)
    }

    suspend fun deleteSubject(subjectId: Long) {
        subjectDao.deleteById(subjectId)
    }
}
