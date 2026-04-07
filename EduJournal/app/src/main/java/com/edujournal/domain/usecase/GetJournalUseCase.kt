package com.edujournal.domain.usecase

import com.edujournal.domain.repository.GradeRepository
import javax.inject.Inject

class GetJournalUseCase @Inject constructor(
    private val repository: GradeRepository
) {

    operator fun invoke(
        groupId: Long,
        subjectId: Long,
        lessonTypeId: Long,
        semesterId: Long
    ) = repository.getJournal(groupId, subjectId, lessonTypeId, semesterId)

}
