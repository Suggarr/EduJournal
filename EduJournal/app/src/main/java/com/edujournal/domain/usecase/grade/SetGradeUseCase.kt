package com.edujournal.domain.usecase.grade

import com.edujournal.domain.model.Grade
import com.edujournal.domain.repository.GradeRepository
import javax.inject.Inject

class SetGradeUseCase @Inject constructor(
    private val repository: GradeRepository
) {
    suspend operator fun invoke(grade: Grade){
        repository.insertGrade(grade)
    }
}



