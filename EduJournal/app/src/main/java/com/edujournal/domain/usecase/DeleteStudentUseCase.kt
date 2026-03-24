package com.edujournal.domain.usecase

import com.edujournal.domain.repository.StudentRepository
import javax.inject.Inject

class DeleteStudentUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(studentId: Long) {
        repository.deleteStudent(studentId)
    }
}