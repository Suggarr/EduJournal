package com.edujournal.domain.usecase

import com.edujournal.domain.model.Student
import com.edujournal.domain.repository.StudentRepository
import javax.inject.Inject

class UpdateStudentUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(student: Student): EntityWriteResult {
        if (!repository.existsById(student.id)) return EntityWriteResult.NOT_FOUND
        if (repository.existsByFullNameInGroupExceptId(
                id = student.id,
                groupId = student.groupId,
                lastName = student.lastName,
                firstName = student.firstName,
                middleName = student.middleName
            )
        ) {
            return EntityWriteResult.DUPLICATE
        }
        repository.updateStudent(student)
        return EntityWriteResult.SUCCESS
    }
}
