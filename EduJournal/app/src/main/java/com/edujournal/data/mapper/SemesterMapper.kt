package com.edujournal.data.mapper

import com.edujournal.data.local.database.entities.SemesterEntity
import com.edujournal.domain.model.Semester
import com.edujournal.domain.model.enum.SemesterSeason

fun SemesterEntity.toDomain(): Semester {
    return Semester(
        id = id,
        season = runCatching { SemesterSeason.valueOf(season) }.getOrDefault(SemesterSeason.AUTUMN),
        year = year
    )
}

fun Semester.toEntity(): SemesterEntity {
    return SemesterEntity(
        id = id,
        season = season.name,
        year = year
    )
}


