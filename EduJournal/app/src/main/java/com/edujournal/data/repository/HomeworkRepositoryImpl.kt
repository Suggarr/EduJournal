package com.edujournal.data.repository

import com.edujournal.data.local.datasource.HomeworkLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.Homework
import com.edujournal.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeworkRepositoryImpl(
    private val localDataSource: HomeworkLocalDataSource
) : HomeworkRepository {

    override fun observeHomework(lessonId: Long): Flow<Homework?> {
        return localDataSource.observeHomework(lessonId)
            .map { it?.toDomain() }
    }

    override fun observeHomeworkLessonIds(lessonIds: List<Long>): Flow<List<Long>> {
        return localDataSource.observeHomeworkLessonIds(lessonIds)
    }

    override suspend fun createHomework(homework: Homework): Long {
        return localDataSource.insert(homework.toEntity())
    }

    override suspend fun updateHomework(homework: Homework): Int {
        return localDataSource.update(homework.toEntity())
    }

    override suspend fun deleteHomework(id: Long) {
        localDataSource.deleteById(id)
    }
}


