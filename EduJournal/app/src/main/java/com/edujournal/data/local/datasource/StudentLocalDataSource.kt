package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.StudentDao
import com.edujournal.data.local.database.entities.StudentEntity
import kotlinx.coroutines.flow.Flow

class StudentLocalDataSource(
    private val studentDao: StudentDao
) {
    fun observeStudents(groupId: Long): Flow<List<StudentEntity>> = studentDao.getByGroup(groupId)

    suspend fun insert(student: StudentEntity): Long {
        return studentDao.insert(student)
    }

    suspend fun update(student: StudentEntity): Int {
        return studentDao.update(student)
    }

    suspend fun delete(id: Long) {
        studentDao.deleteById(id)
    }
}


