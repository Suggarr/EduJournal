package com.edujournal.domain.usecase

import com.edujournal.domain.model.LessonType
import com.edujournal.domain.repository.LessonTypeRepository
import kotlinx.coroutines.flow.Flow

class ObserveLessonTypesUseCase(
    private val repository: LessonTypeRepository
) {
    operator fun invoke(): Flow<List<LessonType>> {
        return repository.observeLessonTypes()
    }
}