package com.edujournal.domain.model

data class HomeworkSubmission(
    val id: Long,
    val homeworkId: Long,
    val studentId: Long,
    val status: HomeworkSubmissionStatus
)
