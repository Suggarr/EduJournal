package com.edujournal.domain.usecase.semester

import com.edujournal.domain.model.Semester
import com.edujournal.domain.model.enum.SemesterSeason
import com.edujournal.domain.repository.SemesterRepository
import javax.inject.Inject

class CreateSemesterUseCase @Inject constructor(
    private val repository: SemesterRepository
) {
    suspend operator fun invoke(season: SemesterSeason, year: Int): Long {
        return repository.createSemester(
            Semester(
                id = 0,
                season = season,
                year = year
            )
        )
    }
}




