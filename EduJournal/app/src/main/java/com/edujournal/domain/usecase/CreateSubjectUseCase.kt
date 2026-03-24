package com.edujournal.domain.usecase

import com.edujournal.domain.model.Subject
import com.edujournal.domain.repository.SubjectRepository
import javax.inject.Inject

class CreateSubjectUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String?
    ){
        val subject = Subject(
            id = 0,
            name = name,
            description = description
        )

        subjectRepository.createSubject(subject)
    }
}