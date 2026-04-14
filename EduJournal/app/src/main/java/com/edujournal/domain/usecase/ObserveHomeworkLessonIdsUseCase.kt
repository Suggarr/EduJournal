package com.edujournal.domain.usecase

import com.edujournal.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHomeworkLessonIdsUseCase @Inject constructor(
    private val repository: HomeworkRepository
) {
    operator fun invoke(lessonIds: List<Long>): Flow<List<Long>> {
        return repository.observeHomeworkLessonIds(lessonIds)
    }
}
