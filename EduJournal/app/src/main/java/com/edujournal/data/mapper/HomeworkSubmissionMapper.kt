package com.edujournal.data.mapper

import com.edujournal.data.local.database.entities.HomeworkSubmissionEntity
import com.edujournal.domain.model.HomeworkSubmission
import com.edujournal.domain.model.HomeworkSubmissionStatus

fun HomeworkSubmissionEntity.toDomain(): HomeworkSubmission {
    return HomeworkSubmission(
        id = id,
        homeworkId = homeworkId,
        studentId = studentId,
        status = HomeworkSubmissionStatus.valueOf(status)
    )
}

fun HomeworkSubmission.toEntity(): HomeworkSubmissionEntity {
    return HomeworkSubmissionEntity(
        id = id,
        homeworkId = homeworkId,
        studentId = studentId,
        status = status.name
    )
}
