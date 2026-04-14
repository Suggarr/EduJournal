package com.edujournal.domain.usecase

import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import javax.inject.Inject

class UpdateSubjectLessonTypeUseCase @Inject constructor(
    private val repository: SubjectLessonTypeRepository
) {
    suspend operator fun invoke(SubjectLessonType: SubjectLessonType): EntityWriteResult {
        if (!repository.existsById(SubjectLessonType.id)) return EntityWriteResult.NOT_FOUND
        if (repository.existsByNameExceptId(SubjectLessonType.subjectId, SubjectLessonType.name, SubjectLessonType.id)) {
            return EntityWriteResult.DUPLICATE
        }
        repository.updateLessonType(SubjectLessonType)
        return EntityWriteResult.SUCCESS
    }
}


