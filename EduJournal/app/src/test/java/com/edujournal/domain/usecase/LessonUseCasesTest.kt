package com.edujournal.domain.usecase

import com.edujournal.domain.model.Lesson
import com.edujournal.domain.repository.LessonRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.lesson.CreateLessonUseCase
import com.edujournal.domain.usecase.lesson.DeleteLessonUseCase
import com.edujournal.domain.usecase.lesson.GetLessonsUseCase
import com.edujournal.domain.usecase.lesson.UpdateLessonUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class LessonUseCasesTest {

    @Test
    fun `create lesson returns success and normalizes topic`() = runBlocking {
        val repo = mockk<LessonRepository>()
        coEvery { repo.insertLesson(any()) } returns 1L
        val useCase = CreateLessonUseCase(repo)
        val lesson = Lesson(1L, 10L, 20L, 2L, LocalDate.of(2026, 5, 1), "  Topic   1  ")

        val result = useCase(lesson)

        assertEquals(EntityWriteResult.SUCCESS, result)
        coVerify { repo.insertLesson(match { it.topic == "Topic 1" }) }
    }

    @Test
    fun `update lesson returns not found when no rows updated`() = runBlocking {
        val repo = mockk<LessonRepository>()
        coEvery { repo.updateLesson(any()) } returns 0
        val useCase = UpdateLessonUseCase(repo)
        val lesson = Lesson(2L, 11L, 21L, 3L, LocalDate.of(2026, 5, 2), "Lab")

        val result = useCase(lesson)

        assertEquals(EntityWriteResult.NOT_FOUND, result)
    }

    @Test
    fun `get lessons returns repository data`() = runBlocking {
        val repo = mockk<LessonRepository>()
        val list = listOf(Lesson(3L, 1L, 2L, 1L, LocalDate.of(2026, 5, 3), "Topic 3"))
        coEvery { repo.observeLessons(1L, 2L, 1L) } returns flowOf(list)
        val useCase = GetLessonsUseCase(repo)

        val lessons = useCase(1L, 2L, 1L).first()

        assertEquals(1, lessons.size)
    }

    @Test
    fun `delete lesson delegates id`() = runBlocking {
        val repo = mockk<LessonRepository>()
        coEvery { repo.deleteLesson(77L) } returns Unit
        val useCase = DeleteLessonUseCase(repo)

        useCase(77L)

        coVerify { repo.deleteLesson(77L) }
    }
}
