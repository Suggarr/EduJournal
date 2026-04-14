package com.edujournal.domain.usecase

import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSubjectLessonTypeByIdUseCase @Inject constructor(
    private val repository: SubjectLessonTypeRepository
) {
    operator fun invoke(id: Long): Flow<SubjectLessonType?> {
        return repository.observeById(id)
    }
}


