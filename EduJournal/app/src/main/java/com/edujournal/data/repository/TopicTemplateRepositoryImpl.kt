package com.edujournal.data.repository

import com.edujournal.data.local.datasource.TopicTemplateLocalDataSource
import com.edujournal.data.mapper.toDomain
import com.edujournal.data.mapper.toEntity
import com.edujournal.domain.model.TopicTemplate
import com.edujournal.domain.repository.TopicTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TopicTemplateRepositoryImpl @Inject constructor(
    private val localDataSource: TopicTemplateLocalDataSource
) : TopicTemplateRepository {

    override fun observeByContext(semesterId: Long, subjectLessonTypeId: Long): Flow<List<TopicTemplate>> {
        return localDataSource.observeByContext(semesterId, subjectLessonTypeId)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun existsOrder(
        semesterId: Long,
        subjectLessonTypeId: Long,
        orderInType: Int,
        excludeId: Long
    ): Boolean {
        return localDataSource.existsOrder(semesterId, subjectLessonTypeId, orderInType, excludeId)
    }

    override suspend fun create(item: TopicTemplate): Long {
        return localDataSource.insert(item.toEntity())
    }

    override suspend fun update(item: TopicTemplate): Int {
        return localDataSource.update(item.toEntity())
    }

    override suspend fun deleteById(id: Long) {
        localDataSource.deleteById(id)
    }
}


