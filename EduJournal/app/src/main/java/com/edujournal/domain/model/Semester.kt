
package com.edujournal.domain.model

import com.edujournal.domain.model.enum.SemesterSeason

data class Semester(
    val id: Long,
    val season: SemesterSeason,
    val year: Int
)



