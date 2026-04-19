package com.edujournal.domain.usecase

import com.edujournal.domain.model.Subject
import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import com.edujournal.domain.repository.SubjectRepository
import com.edujournal.utils.normalizeSpaces
import com.edujournal.utils.normalizeSpacesOrNull
import javax.inject.Inject

class CreateSubjectUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val lessonTypeRepository: SubjectLessonTypeRepository
) {
    suspend operator fun invoke(
        name: String,
        abbreviation: String?,
        semesterIds: List<Long> = emptyList()
    ): EntityWriteResult {
        val normalizedName = name.normalizeSpaces()
        val normalizedAbbreviation = abbreviation.normalizeSpacesOrNull()

        require(semesterIds.isNotEmpty()) { "SEMESTER_REQUIRED" }
        if (subjectRepository.existsByName(normalizedName)) {
            return EntityWriteResult.DUPLICATE
        }
        val subject = Subject(
            id = 0,
            name = normalizedName,
            abbreviation = normalizedAbbreviation
        )

        val subjectId = subjectRepository.createSubject(subject)
        if (subjectId <= 0L) {
            return EntityWriteResult.DUPLICATE
        }
        subjectRepository.replaceSubjectSemesters(subjectId, semesterIds)

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


