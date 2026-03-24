package com.edujournal.domain.usecase

import com.edujournal.domain.model.Student
import com.edujournal.domain.repository.StudentRepository
import javax.inject.Inject

class CreateStudentUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        middleName: String,
        groupId: Long
    ){
        val student = Student(
            id = 0,
            firstName = firstName,
            lastName = lastName,
            middleName = middleName,
            groupId = groupId
        )

        repository.createStudent(student)
    }
}