package com.edujournal.domain.usecase.subjectlessontype

import com.edujournal.domain.repository.SubjectLessonTypeRepository
import javax.inject.Inject

class DeleteSubjectLessonTypeUseCase @Inject constructor(
    private val repository: SubjectLessonTypeRepository
) {
    suspend operator fun invoke(typeId: Long) {
        repository.deleteLessonType(typeId)
    }
}




