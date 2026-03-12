package com.edujournal.domain.usecase

import com.edujournal.domain.repository.SubjectRepository

class ObserveSubjectsUseCase(
    private val repository: SubjectRepository
) {
    operator fun invoke() = repository.observeSubjects()
}