package com.edujournal.domain.usecase

import com.edujournal.domain.model.TopicTemplate
import com.edujournal.domain.repository.TopicTemplateRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.topictemplate.CreateTopicTemplateUseCase
import com.edujournal.domain.usecase.topictemplate.UpdateTopicTemplateUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class TopicTemplateUseCasesTest {
    @Test
    fun `create returns not found when order invalid`() = runBlocking {
        val repo = mockk<TopicTemplateRepository>(relaxed = true)
        val result = CreateTopicTemplateUseCase(repo)(TopicTemplate(0, 1L, 2L, "Topic 1", 0))
        assertEquals(EntityWriteResult.NOT_FOUND, result)
    }

    @Test
    fun `update returns not found when no rows updated`() = runBlocking {
        val repo = mockk<TopicTemplateRepository>()
        coEvery { repo.update(any()) } returns 0
        val result = UpdateTopicTemplateUseCase(repo)(TopicTemplate(5L, 1L, 2L, "Topic", 2))
        assertEquals(EntityWriteResult.NOT_FOUND, result)
    }
}
