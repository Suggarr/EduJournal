package com.edujournal.data.local.datasource

import com.edujournal.data.local.dao.HomeworkDao
import com.edujournal.data.local.database.entities.HomeworkEntity
import kotlinx.coroutines.flow.Flow

class HomeworkLocalDataSource(
    private val homeworkDao: HomeworkDao
) {
    fun observeHomework(lessonId: Long): Flow<HomeworkEntity?> {
        return homeworkDao.observeHomework(lessonId)
    }

    fun observeHomeworkLessonIds(lessonIds: List<Long>): Flow<List<Long>> {
        return homeworkDao.observeHomeworkLessonIds(lessonIds)
    }

    suspend fun insert(homework: HomeworkEntity): Long {
        return homeworkDao.insert(homework)
    }

    suspend fun update(homework: HomeworkEntity): Int {
        return homeworkDao.update(homework)
    }

    suspend fun deleteById(id: Long) {
        homeworkDao.deleteById(id)
    }
}


