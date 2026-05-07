package com.edujournal.domain.usecase.student

import android.database.sqlite.SQLiteConstraintException
import com.edujournal.domain.model.Student
import com.edujournal.domain.repository.StudentRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
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

        return try {
            val updated = repository.updateStudent(normalizedStudent)
            if (updated == 0) EntityWriteResult.NOT_FOUND else EntityWriteResult.SUCCESS
        } catch (_: SQLiteConstraintException) {
            EntityWriteResult.DUPLICATE
        }
    }
}
