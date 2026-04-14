package com.edujournal.data.mapper

import com.edujournal.data.local.database.entities.LessonEntity
import com.edujournal.domain.model.Lesson

fun LessonEntity.toDomain(): Lesson {
    return Lesson(
        id = id,
        groupId = groupId,
        subjectId = subjectId,
        subjectLessonTypeId = subjectLessonTypeId,
        semesterId = semesterId,
        date = date,
        topic = topic
    )
}

fun Lesson.toEntity(): LessonEntity {
    return LessonEntity(
        id = id,
        groupId = groupId,
        subjectId = subjectId,
        subjectLessonTypeId = subjectLessonTypeId,
        semesterId = semesterId,
        date = date,
        topic = topic
    )
}

