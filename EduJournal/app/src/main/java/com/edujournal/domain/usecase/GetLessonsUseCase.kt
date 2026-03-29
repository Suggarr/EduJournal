package com.edujournal.domain.usecase

import com.edujournal.domain.repository.LessonRepository
import javax.inject.Inject

class GetLessonsUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(
        groupId: Long,
        subjectId: Long,
        lessonTypeId: Long
    ) = lessonRepository.observeLessons(groupId, subjectId, lessonTypeId)
}
