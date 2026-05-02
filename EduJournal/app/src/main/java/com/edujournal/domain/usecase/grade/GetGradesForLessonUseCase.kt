package com.edujournal.domain.usecase.grade

import com.edujournal.domain.repository.GradeRepository
import javax.inject.Inject

class GetGradesForLessonUseCase @Inject constructor(
    private val repository: GradeRepository
) {
    operator fun invoke(lessonId: Long) = repository.getGradesForLesson(lessonId)
}



