package com.edujournal.domain.usecase

import com.edujournal.domain.model.Subject
import com.edujournal.domain.repository.SubjectRepository
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
        if (!repository.existsById(normalizedSubject.id)) return EntityWriteResult.NOT_FOUND
        if (repository.existsByNameExceptId(normalizedSubject.name, normalizedSubject.id)) return EntityWriteResult.DUPLICATE
        repository.updateSubject(normalizedSubject)
        if (semesterIds != null) {
            repository.replaceSubjectSemesters(normalizedSubject.id, semesterIds)
        }
        return EntityWriteResult.SUCCESS
    }
}
