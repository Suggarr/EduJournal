package com.edujournal.domain.usecase

import com.edujournal.domain.model.LessonType
import com.edujournal.domain.repository.LessonTypeRepository
import javax.inject.Inject

class CreateLessonTypeUseCase @Inject constructor(
    private val repository: LessonTypeRepository
) {
    suspend operator fun invoke(name: String): EntityWriteResult {
        if (repository.existsByName(name)) return EntityWriteResult.DUPLICATE

        val lessonType = LessonType(
            id = 0,
            name = name);

        repository.createLessonType(lessonType)
        return EntityWriteResult.SUCCESS
    }
}
