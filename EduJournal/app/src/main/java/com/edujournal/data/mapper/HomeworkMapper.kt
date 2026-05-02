package com.edujournal.data.mapper

import com.edujournal.data.local.database.entities.HomeworkEntity
import com.edujournal.domain.model.Homework

fun HomeworkEntity.toDomain(): Homework {
    return Homework(
        id = id,
        lessonId = lessonId,
        text = text
    )
}

fun Homework.toEntity(): HomeworkEntity {
    return HomeworkEntity(
        id = id,
        lessonId = lessonId,
        text = text
    )
}


