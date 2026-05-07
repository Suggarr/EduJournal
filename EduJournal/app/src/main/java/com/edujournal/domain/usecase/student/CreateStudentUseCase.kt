package com.edujournal.domain.usecase.student

import com.edujournal.domain.model.Student
import com.edujournal.domain.repository.StudentRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.utils.normalizeSpaces
import com.edujournal.utils.normalizeSpacesOrNull
import javax.inject.Inject

class CreateStudentUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        middleName: String?,
        groupId: Long
    ): EntityWriteResult {
        val normalizedFirstName = firstName.normalizeSpaces()
        val normalizedLastName = lastName.normalizeSpaces()
        val normalizedMiddleName = middleName.normalizeSpacesOrNull()

        val student = Student(
            id = 0,
            firstName = normalizedFirstName,
            lastName = normalizedLastName,
            middleName = normalizedMiddleName,
            groupId = groupId
        )

        val id = repository.createStudent(student)
        return if (id == -1L) EntityWriteResult.DUPLICATE else EntityWriteResult.SUCCESS
    }
}
