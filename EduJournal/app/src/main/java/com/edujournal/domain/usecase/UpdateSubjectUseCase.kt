package com.edujournal.domain.usecase

import com.edujournal.domain.model.Subject
import com.edujournal.domain.model.SubjectLessonTypeHours
import com.edujournal.domain.repository.SubjectRepository
import com.edujournal.domain.repository.SubjectLessonTypeHoursRepository
import javax.inject.Inject

class UpdateSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository,
    private val subjectLessonTypeHoursRepository: SubjectLessonTypeHoursRepository
) {
    suspend operator fun invoke(
        subject: Subject,
        lessonTypeHours: Map<Long, Double?>
    ): EntityWriteResult {
        if (!repository.existsById(subject.id)) return EntityWriteResult.NOT_FOUND
        if (repository.existsByNameExceptId(subject.name, subject.id)) return EntityWriteResult.DUPLICATE
        repository.updateSubject(subject)
        val hourItems = lessonTypeHours.map { (lessonTypeId, hours) ->
            SubjectLessonTypeHours(
                subjectId = subject.id,
                lessonTypeId = lessonTypeId,
                hours = hours
            )
        }
        subjectLessonTypeHoursRepository.replaceForSubject(subject.id, hourItems)
        return EntityWriteResult.SUCCESS
    }
}
