package com.edujournal.domain.usecase

import com.edujournal.domain.model.Student
import com.edujournal.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow

class ObserveStudentsUseCase(
    private val repository: StudentRepository
) {
    operator fun invoke(groupId: Long): Flow<List<Student>> {
        return repository.observeStudents(groupId)
    }
}