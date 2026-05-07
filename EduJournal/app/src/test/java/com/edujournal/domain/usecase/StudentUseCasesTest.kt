package com.edujournal.domain.usecase

import com.edujournal.domain.model.Student
import com.edujournal.domain.repository.StudentRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.student.CreateStudentUseCase
import com.edujournal.domain.usecase.student.UpdateStudentUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class StudentUseCasesTest {
    @Test
    fun `create student returns duplicate when insert ignored`() = runBlocking {
        val repo = mockk<StudentRepository>()
        coEvery { repo.createStudent(any()) } returns -1L
        val result = CreateStudentUseCase(repo)("Ivan", "Ivanov", "Ivanovich", 1L)
        assertEquals(EntityWriteResult.DUPLICATE, result)
    }

    @Test
    fun `update student returns not found when no rows updated`() = runBlocking {
        val repo = mockk<StudentRepository>()
        coEvery { repo.updateStudent(any()) } returns 0
        val result = UpdateStudentUseCase(repo)(Student(99L, "Ivan", "Ivanov", null, 1L))
        assertEquals(EntityWriteResult.NOT_FOUND, result)
        coVerify(exactly = 1) { repo.updateStudent(any()) }
    }
}
