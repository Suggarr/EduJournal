package com.edujournal.data.mapper

import com.edujournal.data.local.database.entities.SubjectEntity
import com.edujournal.domain.model.Subject

fun SubjectEntity.toDomain(): Subject {
    return Subject(
        id = id,
        name = name,
        description = description
    )
}

fun Subject.toEntity(): SubjectEntity {
    return SubjectEntity(
        id = id,
        name = name,
        description = description
    )
}