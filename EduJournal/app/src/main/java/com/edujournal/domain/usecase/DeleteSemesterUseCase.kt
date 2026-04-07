package com.edujournal.domain.usecase

import com.edujournal.domain.repository.SemesterRepository
import javax.inject.Inject

class DeleteSemesterUseCase @Inject constructor(
    private val repository: SemesterRepository
) {
    suspend operator fun invoke(semesterId: Long) {
        repository.deleteSemester(semesterId)
    }
}
