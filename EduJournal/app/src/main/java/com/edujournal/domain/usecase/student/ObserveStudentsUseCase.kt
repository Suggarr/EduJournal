package com.edujournal.domain.usecase.student

import com.edujournal.domain.model.Student
import com.edujournal.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveStudentsUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    operator fun invoke(groupId: Long): Flow<List<Student>> {
        return repository.observeStudents(groupId)
    }
}



