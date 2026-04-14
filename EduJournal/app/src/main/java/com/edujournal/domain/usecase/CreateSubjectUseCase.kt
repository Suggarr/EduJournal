package com.edujournal.domain.usecase

import com.edujournal.domain.model.Subject
import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import com.edujournal.domain.repository.SubjectRepository
import javax.inject.Inject

class CreateSubjectUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val lessonTypeRepository: SubjectLessonTypeRepository
) {
    suspend operator fun invoke(name: String, abbreviation: String?): EntityWriteResult {
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

        val defaultTypes = listOf(
            SubjectLessonType(id = 0, subjectId = subjectId, name = "Лекция", hours = 20.0),
            SubjectLessonType(id = 0, subjectId = subjectId, name = "Практика", hours = 30.0),
            SubjectLessonType(id = 0, subjectId = subjectId, name = "Лабораторная", hours = 10.0)
        )
        defaultTypes.forEach { type ->
            lessonTypeRepository.createLessonType(type)
        }
        return EntityWriteResult.SUCCESS
    }
}


