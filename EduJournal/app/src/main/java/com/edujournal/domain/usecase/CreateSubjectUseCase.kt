package com.edujournal.domain.usecase

import com.edujournal.domain.model.Subject
import com.edujournal.domain.model.SubjectLessonTypeHours
import com.edujournal.domain.repository.SubjectRepository
import com.edujournal.domain.repository.SubjectLessonTypeHoursRepository
import javax.inject.Inject

class CreateSubjectUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val subjectLessonTypeHoursRepository: SubjectLessonTypeHoursRepository
) {
    suspend operator fun invoke(
        name: String,
        abbreviation: String?,
        lessonTypeHours: Map<Long, Double?>
    ): EntityWriteResult{
        if (subjectRepository.existsByName(name)) {
            return EntityWriteResult.DUPLICATE
        }
        val subject = Subject(
            id = 0,
            name = name,
            abbreviation = abbreviation
        )

        val subjectId = subjectRepository.createSubject(subject)
        if (subjectId <= 0L) {
            return EntityWriteResult.DUPLICATE
        }
        val hourItems = lessonTypeHours.map { (lessonTypeId, hours) ->
            SubjectLessonTypeHours(
                subjectId = subjectId,
                lessonTypeId = lessonTypeId,
                hours = hours
            )
        }
        subjectLessonTypeHoursRepository.replaceForSubject(subjectId, hourItems)
        return EntityWriteResult.SUCCESS
    }
}
