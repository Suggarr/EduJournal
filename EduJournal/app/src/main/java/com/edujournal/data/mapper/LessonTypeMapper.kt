package com.edujournal.data.mapper

import com.edujournal.data.local.database.entities.LessonTypeEntity
import com.edujournal.domain.model.LessonType

fun LessonTypeEntity.toDomain(): LessonType {
    return LessonType(
        id = id,
        name = name
    )
}

fun LessonType.toEntity(): LessonTypeEntity {
    return LessonTypeEntity(
        id = id,
        name = name
    )
}