package com.edujournal.domain.usecase.subject

import com.edujournal.domain.repository.SubjectRepository
import javax.inject.Inject

class ObserveSubjectsUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    operator fun invoke() = repository.observeSubjects()
    operator fun invoke(semesterId: Long) = repository.observeSubjectsBySemester(semesterId)
}




