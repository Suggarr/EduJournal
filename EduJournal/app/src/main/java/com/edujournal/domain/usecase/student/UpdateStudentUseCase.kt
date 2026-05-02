package com.edujournal.domain.usecase.student
import com.edujournal.domain.usecase.common.EntityWriteResult

import com.edujournal.domain.model.Student
import com.edujournal.domain.repository.StudentRepository
import com.edujournal.utils.normalizeSpaces
import com.edujournal.utils.normalizeSpacesOrNull
import javax.inject.Inject

class UpdateStudentUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(student: Student): EntityWriteResult {
        val normalizedStudent = student.copy(
            firstName = student.firstName.normalizeSpaces(),
            lastName = student.lastName.normalizeSpaces(),
            middleName = student.middleName.normalizeSpacesOrNull()
        )

        if (!repository.existsById(normalizedStudent.id)) return EntityWriteResult.NOT_FOUND
        if (repository.existsByFullNameInGroupExceptId(
                id = normalizedStudent.id,
                groupId = normalizedStudent.groupId,
                lastName = normalizedStudent.lastName,
                firstName = normalizedStudent.firstName,
                middleName = normalizedStudent.middleName
            )
        ) {
            return EntityWriteResult.DUPLICATE
        }
        repository.updateStudent(normalizedStudent)
        return EntityWriteResult.SUCCESS
    }
}




