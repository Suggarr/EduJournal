package com.edujournal.domain.usecase

import android.database.sqlite.SQLiteConstraintException
import com.edujournal.domain.model.TopicTemplate
import com.edujournal.domain.repository.TopicTemplateRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.topictemplate.CreateTopicTemplateUseCase
import com.edujournal.domain.usecase.topictemplate.UpdateTopicTemplateUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class TopicTemplateUseCasesTest {
    @Test
    fun `create returns success when valid and inserted`() = runBlocking {
        val repo = mockk<TopicTemplateRepository>()
        coEvery { repo.create(any()) } returns 12L
        val result = CreateTopicTemplateUseCase(repo)(
            TopicTemplate(0, 1L, 2L, "Topic 1", 1)
        )
        assertEquals(EntityWriteResult.SUCCESS, result)
    }

    @Test
    fun `create normalizes title before save`() = runBlocking {
        val repo = mockk<TopicTemplateRepository>()
        coEvery { repo.create(any()) } returns 13L

        val result = CreateTopicTemplateUseCase(repo)(
            TopicTemplate(0, 1L, 2L, "  Topic   Name  ", 1)
        )

        assertEquals(EntityWriteResult.SUCCESS, result)
        coVerify { repo.create(match { it.title == "Topic Name" }) }
    }

    @Test
    fun `create returns not found when order invalid`() = runBlocking {
        val repo = mockk<TopicTemplateRepository>(relaxed = true)
        val result = CreateTopicTemplateUseCase(repo)(TopicTemplate(0, 1L, 2L, "Topic 1", 0))
        assertEquals(EntityWriteResult.NOT_FOUND, result)
    }

    @Test
    fun `update returns success when row updated`() = runBlocking {
        val repo = mockk<TopicTemplateRepository>()
        coEvery { repo.update(any()) } returns 1
        val result = UpdateTopicTemplateUseCase(repo)(TopicTemplate(5L, 1L, 2L, "Topic", 2))
        assertEquals(EntityWriteResult.SUCCESS, result)
    }

    @Test
    fun `update returns not found when no rows updated`() = runBlocking {
        val repo = mockk<TopicTemplateRepository>()
        coEvery { repo.update(any()) } returns 0
        val result = UpdateTopicTemplateUseCase(repo)(TopicTemplate(5L, 1L, 2L, "Topic", 2))
        assertEquals(EntityWriteResult.NOT_FOUND, result)
    }

    @Test
    fun `update returns duplicate when constraint exception thrown`() = runBlocking {
        val repo = mockk<TopicTemplateRepository>()
        coEvery { repo.update(any()) } throws SQLiteConstraintException()
        val result = UpdateTopicTemplateUseCase(repo)(TopicTemplate(5L, 1L, 2L, "Topic", 2))
        assertEquals(EntityWriteResult.DUPLICATE, result)
    }
}
