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

    suspend fun existsById(id: Long): Boolean {
        return studentDao.existsById(id)
    }

    suspend fun existsByFullNameInGroup(
        groupId: Long,
        lastName: String,
        firstName: String,
        middleName: String?
    ): Boolean {
        return studentDao.existsByFullNameInGroup(groupId, lastName, firstName, middleName)
    }

    suspend fun existsByFullNameInGroupExceptId(
        id: Long,
        groupId: Long,
        lastName: String,
        firstName: String,
        middleName: String?
    ): Boolean {
        return studentDao.existsByFullNameInGroupExceptId(id, groupId, lastName, firstName, middleName)
    }

    suspend fun delete(id: Long) {
        studentDao.deleteById(id)
    }
}


