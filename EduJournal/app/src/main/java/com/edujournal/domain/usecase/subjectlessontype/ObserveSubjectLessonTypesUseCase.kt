package com.edujournal.domain.usecase.subjectlessontype

import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSubjectLessonTypesUseCase @Inject constructor(
    private val repository: SubjectLessonTypeRepository
) {
    operator fun invoke(subjectId: Long): Flow<List<SubjectLessonType>> {
        return repository.observeLessonTypes(subjectId)
    }
}






