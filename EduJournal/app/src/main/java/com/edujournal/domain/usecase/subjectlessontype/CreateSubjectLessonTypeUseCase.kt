package com.edujournal.domain.usecase.subjectlessontype
import com.edujournal.domain.usecase.common.EntityWriteResult

import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class CreateSubjectLessonTypeUseCase @Inject constructor(
    private val repository: SubjectLessonTypeRepository
) {
    suspend operator fun invoke(subjectId: Long, name: String, hours: Double?): EntityWriteResult {
        val normalizedName = name.normalizeSpaces()
        if (repository.existsByName(subjectId, normalizedName)) return EntityWriteResult.DUPLICATE

        val SubjectLessonType = SubjectLessonType(
            id = 0,
            subjectId = subjectId,
            name = normalizedName,
            hours = hours
        )

        repository.createLessonType(SubjectLessonType)
        return EntityWriteResult.SUCCESS
    }
}






