package com.edujournal.domain.usecase

import com.edujournal.domain.model.LessonType
import com.edujournal.domain.repository.LessonTypeRepository
import javax.inject.Inject

class UpdateLessonTypeUseCase @Inject constructor(
    private val repository: LessonTypeRepository
) {
    suspend operator fun invoke(lessonType: LessonType): EntityWriteResult {
        if (!repository.existsById(lessonType.id)) return EntityWriteResult.NOT_FOUND
        if (repository.existsByNameExceptId(lessonType.name, lessonType.id)) return EntityWriteResult.DUPLICATE
        repository.updateLessonType(lessonType)
        return EntityWriteResult.SUCCESS
    }
}
