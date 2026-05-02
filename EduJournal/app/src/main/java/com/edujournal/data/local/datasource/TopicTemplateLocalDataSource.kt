package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.TopicTemplateDao
import com.edujournal.data.local.database.entities.TopicTemplateEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TopicTemplateLocalDataSource @Inject constructor(
    private val dao: TopicTemplateDao
) {
    fun observeByContext(semesterId: Long, subjectLessonTypeId: Long): Flow<List<TopicTemplateEntity>> {
        return dao.observeByContext(semesterId, subjectLessonTypeId)
    }

    suspend fun existsOrder(
        semesterId: Long,
        subjectLessonTypeId: Long,
        orderInType: Int,
        excludeId: Long = 0
    ): Boolean {
        return dao.existsOrder(semesterId, subjectLessonTypeId, orderInType, excludeId)
    }

    suspend fun insert(item: TopicTemplateEntity): Long = dao.insert(item)

    suspend fun update(item: TopicTemplateEntity): Int = dao.update(item)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}


