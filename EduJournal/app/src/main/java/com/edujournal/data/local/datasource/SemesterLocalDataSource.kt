package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.SemesterDao
import com.edujournal.data.local.database.entities.SemesterEntity
import kotlinx.coroutines.flow.Flow

class SemesterLocalDataSource(
    private val semesterDao: SemesterDao
) {
    fun observeSemesters(): Flow<List<SemesterEntity>> {
        return semesterDao.observeSemesters()
    }

    suspend fun insertSemester(semester: SemesterEntity): Long {
        return semesterDao.insert(semester)
    }

    suspend fun updateSemester(semester: SemesterEntity) {
        semesterDao.update(semester)
    }

    suspend fun deleteSemester(semesterId: Long) {
        semesterDao.deleteById(semesterId)
    }
}


