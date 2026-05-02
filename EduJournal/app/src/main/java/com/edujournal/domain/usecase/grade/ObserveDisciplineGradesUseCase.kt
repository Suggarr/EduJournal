package com.edujournal.domain.usecase.grade

import com.edujournal.domain.repository.GradeRepository
import javax.inject.Inject

class ObserveDisciplineGradesUseCase @Inject constructor(
    private val repository: GradeRepository
) {
    operator fun invoke(
        groupId: Long,
        subjectId: Long,
        semesterId: Long
    ) = repository.observeDisciplineGrades(groupId, subjectId, semesterId)
}




