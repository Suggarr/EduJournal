package com.edujournal.data.mapper

import com.edujournal.data.local.database.entities.SubjectLessonTypeEntity
import com.edujournal.domain.model.SubjectLessonType

fun SubjectLessonTypeEntity.toDomain(): SubjectLessonType {
    return SubjectLessonType(
        id = id,
        subjectId = subjectId,
        name = name,
        hours = hours
    )
}

fun SubjectLessonType.toEntity(): SubjectLessonTypeEntity {
    return SubjectLessonTypeEntity(
        id = id,
        subjectId = subjectId,
        name = name,
        hours = hours
    )
}




