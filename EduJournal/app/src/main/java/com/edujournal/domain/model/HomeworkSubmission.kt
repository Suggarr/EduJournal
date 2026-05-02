
package com.edujournal.domain.model

import com.edujournal.domain.model.enum.HomeworkSubmissionStatus

data class HomeworkSubmission(
    val id: Long,
    val homeworkId: Long,
    val studentId: Long,
    val status: HomeworkSubmissionStatus
)



