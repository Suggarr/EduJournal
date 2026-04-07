package com.edujournal.domain.model

data class Semester(
    val id: Long,
    val season: SemesterSeason,
    val year: Int
)
