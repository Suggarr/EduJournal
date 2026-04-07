package com.edujournal.data.repository

import com.edujournal.data.local.datasource.SemesterLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.Semester
import com.edujournal.domain.repository.SemesterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SemesterRepositoryImpl(
    private val localDataSource: SemesterLocalDataSource
) : SemesterRepository {
    override fun observeSemesters(): Flow<List<Semester>> {
        return localDataSource.observeSemesters().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun createSemester(semester: Semester): Long {
        return localDataSource.insertSemester(semester.toEntity())
    }

    override suspend fun updateSemester(semester: Semester) {
        localDataSource.updateSemester(semester.toEntity())
    }

    override suspend fun deleteSemester(semesterId: Long) {
        localDataSource.deleteSemester(semesterId)
    }
}
