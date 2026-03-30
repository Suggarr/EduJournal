package com.edujournal.data.mapper

import com.edujournal.data.local.database.entities.SubjectLessonTypeHoursEntity
import com.edujournal.domain.model.SubjectLessonTypeHours

fun SubjectLessonTypeHoursEntity.toDomain(): SubjectLessonTypeHours {
    return SubjectLessonTypeHours(
        id = id,
        subjectId = subjectId,
        lessonTypeId = lessonTypeId,
        hours = hours
    )
}

fun SubjectLessonTypeHours.toEntity(): SubjectLessonTypeHoursEntity {
    return SubjectLessonTypeHoursEntity(
        id = id,
        subjectId = subjectId,
        lessonTypeId = lessonTypeId,
        hours = hours
    )
}
