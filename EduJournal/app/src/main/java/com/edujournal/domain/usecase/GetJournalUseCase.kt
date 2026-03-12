package com.edujournal.domain.usecase

import com.edujournal.domain.repository.GradeRepository

class GetJournalUseCase(
    private val repository: GradeRepository
) {

    operator fun invoke(groupId: Long) =
        repository.getJournal(groupId)

}