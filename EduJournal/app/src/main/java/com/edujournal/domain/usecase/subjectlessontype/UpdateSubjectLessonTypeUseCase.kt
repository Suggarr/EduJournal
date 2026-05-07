package com.edujournal.domain.usecase.subjectlessontype

import android.database.sqlite.SQLiteConstraintException
import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class UpdateSubjectLessonTypeUseCase @Inject constructor(
    private val repository: SubjectLessonTypeRepository
) {
    suspend operator fun invoke(subjectLessonType: SubjectLessonType): EntityWriteResult {
        val normalizedLessonType = subjectLessonType.copy(
            name = subjectLessonType.name.normalizeSpaces()
        )

        return try {
            val updated = repository.updateLessonType(normalizedLessonType)
            if (updated == 0) EntityWriteResult.NOT_FOUND else EntityWriteResult.SUCCESS
        } catch (_: SQLiteConstraintException) {
            EntityWriteResult.DUPLICATE
        }
    }
}
