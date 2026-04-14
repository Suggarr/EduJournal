package com.edujournal.domain.usecase

import com.edujournal.domain.model.Subject
import com.edujournal.domain.repository.SubjectRepository
import javax.inject.Inject

class UpdateSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(
        subject: Subject,
        semesterIds: List<Long>? = null
    ): EntityWriteResult {
        if (semesterIds != null) {
            require(semesterIds.isNotEmpty()) { "SEMESTER_REQUIRED" }
        }
        if (!repository.existsById(subject.id)) return EntityWriteResult.NOT_FOUND
        if (repository.existsByNameExceptId(subject.name, subject.id)) return EntityWriteResult.DUPLICATE
        repository.updateSubject(subject)
        if (semesterIds != null) {
            repository.replaceSubjectSemesters(subject.id, semesterIds)
        }
        return EntityWriteResult.SUCCESS
    }
}
