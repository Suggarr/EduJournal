package com.edujournal.domain.usecase

import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class UpdateSubjectLessonTypeUseCase @Inject constructor(
    private val repository: SubjectLessonTypeRepository
) {
    suspend operator fun invoke(SubjectLessonType: SubjectLessonType): EntityWriteResult {
        val normalizedLessonType = SubjectLessonType.copy(
            name = SubjectLessonType.name.normalizeSpaces()
        )

        if (!repository.existsById(normalizedLessonType.id)) return EntityWriteResult.NOT_FOUND
        if (repository.existsByNameExceptId(normalizedLessonType.subjectId, normalizedLessonType.name, normalizedLessonType.id)) {
            return EntityWriteResult.DUPLICATE
        }
        repository.updateLessonType(normalizedLessonType)
        return EntityWriteResult.SUCCESS
    }
}


