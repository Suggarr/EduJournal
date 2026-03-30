package com.edujournal.domain.usecase

import com.edujournal.domain.repository.SubjectLessonTypeHoursRepository
import javax.inject.Inject

class ObserveSubjectLessonTypeHoursUseCase @Inject constructor(
    private val repository: SubjectLessonTypeHoursRepository
) {
    operator fun invoke() = repository.observeAll()
}
