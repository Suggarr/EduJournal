package com.edujournal.domain.usecase

import com.edujournal.domain.repository.LessonTypeRepository
import javax.inject.Inject

class DeleteLessonTypeUseCase @Inject constructor(
    private val repository: LessonTypeRepository
) {
    suspend operator fun invoke(typeId: Long) {
        repository.deleteLessonType(typeId)
    }
}