package com.edujournal.domain.usecase

import com.edujournal.domain.repository.SubjectRepository
import javax.inject.Inject

class ObserveSubjectSemesterIdsUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    operator fun invoke(subjectId: Long) = repository.observeSemesterIdsBySubject(subjectId)
}

