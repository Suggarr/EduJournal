package com.edujournal.data.mapper

import com.edujournal.data.local.database.entities.GradeEntity
import com.edujournal.domain.model.Grade
import com.edujournal.domain.model.enum.GradeType

fun GradeEntity.toDomain(): Grade {
    return Grade(
        id = id,
        studentId = studentId,
        lessonId = lessonId,
        value = value,
        type = GradeType.valueOf(type),
        comment = comment
    )
}

fun Grade.toEntity(): GradeEntity {
    return GradeEntity(
        id = id,
        studentId = studentId,
        lessonId = lessonId,
        value = value,
        type = type.name,
        comment = comment
    )
}

