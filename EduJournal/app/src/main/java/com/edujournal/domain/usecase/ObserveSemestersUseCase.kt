package com.edujournal.domain.usecase

import com.edujournal.domain.repository.SemesterRepository
import javax.inject.Inject

class ObserveSemestersUseCase @Inject constructor(
    private val repository: SemesterRepository
) {
    operator fun invoke() = repository.observeSemesters()
}
