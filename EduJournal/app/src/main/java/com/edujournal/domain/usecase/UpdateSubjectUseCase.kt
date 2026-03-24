package com.edujournal.domain.usecase

import com.edujournal.domain.model.Subject
import com.edujournal.domain.repository.SubjectRepository
import javax.inject.Inject

class UpdateSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(subject: Subject) {
        repository.updateSubject(subject)
    }
}