package com.edujournal.domain.usecase

import com.edujournal.domain.model.Semester
import com.edujournal.domain.model.enum.SemesterSeason
import com.edujournal.domain.repository.SemesterRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.semester.CreateSemesterUseCase
import com.edujournal.domain.usecase.semester.UpdateSemesterUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SemesterUseCasesTest {
    @Test
    fun `create semester returns duplicate when insert ignored`() = runBlocking {
        val repo = mockk<SemesterRepository>()
        coEvery { repo.createSemester(any()) } returns -1L
        val result = CreateSemesterUseCase(repo)(SemesterSeason.SPRING, 2027)
        assertEquals(EntityWriteResult.DUPLICATE, result)
    }

    @Test
    fun `update semester returns not found when no rows`() = runBlocking {
        val repo = mockk<SemesterRepository>()
        coEvery { repo.updateSemester(any()) } returns 0
        val result = UpdateSemesterUseCase(repo)(Semester(5L, SemesterSeason.AUTUMN, 2026))
        assertEquals(EntityWriteResult.NOT_FOUND, result)
    }
}
