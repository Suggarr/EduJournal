package com.edujournal.domain.usecase

import com.edujournal.domain.model.Student
import com.edujournal.domain.repository.StudentRepository
import com.edujournal.utils.normalizeSpaces
import com.edujournal.utils.normalizeSpacesOrNull
import javax.inject.Inject

class CreateStudentUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        middleName: String,
        groupId: Long
    ): EntityWriteResult{
        val normalizedFirstName = firstName.normalizeSpaces()
        val normalizedLastName = lastName.normalizeSpaces()
        val normalizedMiddleName = middleName.normalizeSpacesOrNull().orEmpty()

        if (repository.existsByFullNameInGroup(groupId, normalizedLastName, normalizedFirstName, normalizedMiddleName)) {
            return EntityWriteResult.DUPLICATE
        }
        val student = Student(
            id = 0,
            firstName = normalizedFirstName,
            lastName = normalizedLastName,
            middleName = normalizedMiddleName,
            groupId = groupId
        )

        repository.createStudent(student)
        return EntityWriteResult.SUCCESS
    }
}
