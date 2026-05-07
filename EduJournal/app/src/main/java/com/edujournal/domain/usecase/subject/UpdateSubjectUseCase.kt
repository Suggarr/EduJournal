package com.edujournal.domain.usecase.subject

import android.database.sqlite.SQLiteConstraintException
import com.edujournal.domain.model.Subject
import com.edujournal.domain.repository.SubjectRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.utils.normalizeSpaces
import com.edujournal.utils.normalizeSpacesOrNull
import javax.inject.Inject

class UpdateSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(
        subject: Subject,
        semesterIds: List<Long>? = null
    ): EntityWriteResult {
        val normalizedSubject = subject.copy(
            name = subject.name.normalizeSpaces(),
            abbreviation = subject.abbreviation.normalizeSpacesOrNull()
        )

        if (semesterIds != null) {
            require(semesterIds.isNotEmpty()) { "SEMESTER_REQUIRED" }
        }

        return try {
            val updated = repository.updateSubject(normalizedSubject)
            if (updated == 0) {
                EntityWriteResult.NOT_FOUND
            } else {
                if (semesterIds != null) {
                    repository.replaceSubjectSemesters(normalizedSubject.id, semesterIds)
                }
                EntityWriteResult.SUCCESS
            }
        } catch (_: SQLiteConstraintException) {
            EntityWriteResult.DUPLICATE
        }
    }
}
