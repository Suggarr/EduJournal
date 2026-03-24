package com.edujournal.domain.usecase

import com.edujournal.domain.model.LessonType
import com.edujournal.domain.repository.LessonTypeRepository
import javax.inject.Inject

class UpdateLessonTypeUseCase @Inject constructor(
    private val repository: LessonTypeRepository
) {
    suspend operator fun invoke(lessonType: LessonType) {
        repository.updateLessonType(lessonType)
    }
}