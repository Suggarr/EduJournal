package com.edujournal.domain.usecase

import com.edujournal.domain.model.LessonType
import com.edujournal.domain.repository.LessonTypeRepository

class CreateLessonTypeUseCase(
    private val repository: LessonTypeRepository
) {
    suspend operator fun invoke(name: String) {

        val lessonType = LessonType(
            id = 0,
            name = name);

        repository.createLessonType(lessonType)
    }
}