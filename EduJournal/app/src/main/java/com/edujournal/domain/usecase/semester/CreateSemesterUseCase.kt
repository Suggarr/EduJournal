package com.edujournal.domain.usecase.semester

import com.edujournal.domain.model.Semester
import com.edujournal.domain.model.enum.SemesterSeason
import com.edujournal.domain.repository.SemesterRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import javax.inject.Inject

class CreateSemesterUseCase @Inject constructor(
    private val repository: SemesterRepository
) {
    suspend operator fun invoke(season: SemesterSeason, year: Int): EntityWriteResult {
        require(year > 0) { "INVALID_YEAR" }
        val id = repository.createSemester(
            Semester(
                id = 0,
                season = season,
                year = year
            )
        )
        return if (id == -1L) EntityWriteResult.DUPLICATE else EntityWriteResult.SUCCESS
    }
}




