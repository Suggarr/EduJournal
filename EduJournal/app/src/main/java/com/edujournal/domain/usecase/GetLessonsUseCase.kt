package com.edujournal.domain.usecase

import com.edujournal.domain.repository.LessonRepository
import javax.inject.Inject

class GetLessonsUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(
        groupId: Long,
        subjectLessonTypeId: Long,
        semesterId: Long
    ) = lessonRepository.observeLessons(groupId, subjectLessonTypeId, semesterId)
}

