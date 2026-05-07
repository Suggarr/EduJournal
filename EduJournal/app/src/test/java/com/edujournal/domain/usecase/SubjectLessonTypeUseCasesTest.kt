package com.edujournal.domain.usecase

import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.subjectlessontype.CreateSubjectLessonTypeUseCase
import com.edujournal.domain.usecase.subjectlessontype.UpdateSubjectLessonTypeUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SubjectLessonTypeUseCasesTest {
    @Test
    fun `create returns duplicate when insert ignored`() = runBlocking {
        val repo = mockk<SubjectLessonTypeRepository>()
        coEvery { repo.createLessonType(any()) } returns -1L
        val result = CreateSubjectLessonTypeUseCase(repo)(1L, "Lecture", 20.0)
        assertEquals(EntityWriteResult.DUPLICATE, result)
    }

    @Test
    fun `update returns not found when no rows updated`() = runBlocking {
        val repo = mockk<SubjectLessonTypeRepository>()
        coEvery { repo.updateLessonType(any()) } returns 0
        val result = UpdateSubjectLessonTypeUseCase(repo)(SubjectLessonType(9L, 1L, "Practice", 8.0))
        assertEquals(EntityWriteResult.NOT_FOUND, result)
    }
}
