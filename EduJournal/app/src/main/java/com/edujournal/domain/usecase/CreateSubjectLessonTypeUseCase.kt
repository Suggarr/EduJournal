package com.edujournal.domain.usecase

import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import javax.inject.Inject

class CreateSubjectLessonTypeUseCase @Inject constructor(
    private val repository: SubjectLessonTypeRepository
) {
    suspend operator fun invoke(subjectId: Long, name: String, hours: Double?): EntityWriteResult {
        if (repository.existsByName(subjectId, name)) return EntityWriteResult.DUPLICATE

        val SubjectLessonType = SubjectLessonType(
            id = 0,
            subjectId = subjectId,
            name = name,
            hours = hours
        )

        repository.createLessonType(SubjectLessonType)
        return EntityWriteResult.SUCCESS
    }
}


