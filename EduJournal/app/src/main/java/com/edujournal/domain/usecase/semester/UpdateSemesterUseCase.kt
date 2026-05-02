package com.edujournal.domain.usecase.semester

import com.edujournal.domain.model.Semester
import com.edujournal.domain.repository.SemesterRepository
import javax.inject.Inject

class UpdateSemesterUseCase @Inject constructor(
    private val repository: SemesterRepository
) {
    suspend operator fun invoke(semester: Semester) {
        repository.updateSemester(semester)
    }
}




