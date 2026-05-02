package com.edujournal.domain.usecase.subjectlessontype

import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import javax.inject.Inject

class GetSubjectLessonTypeByIdUseCase @Inject constructor(
    private val repository: SubjectLessonTypeRepository
) {
    suspend operator fun invoke(id: Long): SubjectLessonType? {
        return repository.getById(id)
    }
}






