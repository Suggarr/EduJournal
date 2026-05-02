package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.HomeworkSubmissionDao
import com.edujournal.data.local.database.entities.HomeworkSubmissionEntity
import kotlinx.coroutines.flow.Flow

class HomeworkSubmissionLocalDataSource(
    private val homeworkSubmissionDao: HomeworkSubmissionDao
) {
    fun observeSubmissions(homeworkId: Long): Flow<List<HomeworkSubmissionEntity>> {
        return homeworkSubmissionDao.observeSubmissions(homeworkId)
    }

    suspend fun upsert(submission: HomeworkSubmissionEntity) {
        homeworkSubmissionDao.upsert(submission)
    }
}


