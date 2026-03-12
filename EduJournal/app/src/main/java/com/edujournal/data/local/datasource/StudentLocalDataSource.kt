package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.StudentDao
import com.edujournal.data.local.database.entities.StudentEntity
import kotlinx.coroutines.flow.Flow

class StudentLocalDataSource(
    private val studentDao: StudentDao
) {
    fun observeStudents(groupId: Long): Flow<List<StudentEntity>> = studentDao.observeStudents(groupId)

    suspend fun insert(student: StudentEntity) {
        studentDao.insert(student)
    }
}