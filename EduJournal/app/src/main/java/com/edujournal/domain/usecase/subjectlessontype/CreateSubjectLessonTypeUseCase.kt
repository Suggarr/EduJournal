package com.edujournal.domain.usecase.subjectlessontype

import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class CreateSubjectLessonTypeUseCase @Inject constructor(
    private val repository: SubjectLessonTypeRepository
) {
    suspend operator fun invoke(subjectId: Long, name: String, hours: Double?): EntityWriteResult {
        val normalizedName = name.normalizeSpaces()

        val subjectLessonType = SubjectLessonType(
            id = 0,
            subjectId = subjectId,
            name = normalizedName,
            hours = hours
        )

        val id = repository.createLessonType(subjectLessonType)
        return if (id == -1L) EntityWriteResult.DUPLICATE else EntityWriteResult.SUCCESS
    }
}
