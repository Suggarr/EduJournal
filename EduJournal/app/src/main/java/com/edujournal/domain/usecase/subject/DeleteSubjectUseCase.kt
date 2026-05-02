package com.edujournal.domain.usecase.subject

import com.edujournal.domain.repository.SubjectRepository
import javax.inject.Inject

class DeleteSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(subjectId: Long) {
        repository.deleteSubject(subjectId)
    }
}



