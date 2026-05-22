package com.edujournal.domain.usecase

import com.edujournal.domain.model.Subject
import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.repository.SubjectLessonTypeRepository
import com.edujournal.domain.repository.SubjectRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.subject.CreateSubjectUseCase
import com.edujournal.domain.usecase.subject.UpdateSubjectUseCase
import io.mockk.coVerify
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import android.database.sqlite.SQLiteConstraintException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SubjectUseCasesTest {
    @Test
    fun `create subject throws when semester list is empty`() {
        runBlocking {
            val subjectRepo = mockk<SubjectRepository>(relaxed = true)
            val typeRepo = mockk<SubjectLessonTypeRepository>(relaxed = true)
            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { CreateSubjectUseCase(subjectRepo, typeRepo)("Databases", "DB", emptyList()) }
            }
        }
    }

    @Test
    fun `create subject returns duplicate when insert ignored`() = runBlocking {
        val subjectRepo = mockk<SubjectRepository>()
        val typeRepo = mockk<SubjectLessonTypeRepository>(relaxed = true)
        coEvery { subjectRepo.createSubject(any()) } returns -1L
        val result = CreateSubjectUseCase(subjectRepo, typeRepo)("Databases", "DB", listOf(1L))
        assertEquals(EntityWriteResult.DUPLICATE, result)
    }

    @Test
    fun `create subject creates default lesson types`() = runBlocking {
        val subjectRepo = mockk<SubjectRepository>()
        val typeRepo = mockk<SubjectLessonTypeRepository>()
        val createdTypes = mutableListOf<SubjectLessonType>()
        coEvery { subjectRepo.createSubject(any()) } returns 15L
        coEvery { subjectRepo.replaceSubjectSemesters(15L, listOf(2L, 3L)) } returns Unit
        coEvery { typeRepo.createLessonType(capture(createdTypes)) } returns 1L
        val result = CreateSubjectUseCase(subjectRepo, typeRepo)("Databases", "DB", listOf(2L, 3L))
        assertEquals(EntityWriteResult.SUCCESS, result)
        assertEquals(3, createdTypes.size)
        assertTrue(createdTypes.all { it.subjectId == 15L })
    }

    @Test
    fun `create subject normalizes name and abbreviation`() = runBlocking {
        val subjectRepo = mockk<SubjectRepository>()
        val typeRepo = mockk<SubjectLessonTypeRepository>(relaxed = true)
        coEvery { subjectRepo.createSubject(any()) } returns 21L
        coEvery { subjectRepo.replaceSubjectSemesters(any(), any()) } returns Unit

        val result = CreateSubjectUseCase(subjectRepo, typeRepo)(
            "  Databases   and   SQL  ",
            "  DB   SQL  ",
            listOf(1L)
        )

        assertEquals(EntityWriteResult.SUCCESS, result)
        coVerify {
            subjectRepo.createSubject(match {
                it.name == "Databases and SQL" && it.abbreviation == "DB SQL"
            })
        }
    }

    @Test
    fun `update subject returns not found when no rows updated`() = runBlocking {
        val repo = mockk<SubjectRepository>()
        coEvery { repo.updateSubject(any()) } returns 0
        val result = UpdateSubjectUseCase(repo)(Subject(5L, "OOP", "OOP"), listOf(4L))
        assertEquals(EntityWriteResult.NOT_FOUND, result)
    }

    @Test
    fun `update subject returns duplicate when constraint exception thrown`() = runBlocking {
        val repo = mockk<SubjectRepository>()
        coEvery { repo.updateSubject(any()) } throws SQLiteConstraintException()
        val result = UpdateSubjectUseCase(repo)(Subject(5L, "OOP", "OOP"), listOf(4L))
        assertEquals(EntityWriteResult.DUPLICATE, result)
    }

    @Test
    fun `update subject normalizes fields and updates semesters`() = runBlocking {
        val repo = mockk<SubjectRepository>()
        coEvery { repo.updateSubject(any()) } returns 1
        coEvery { repo.replaceSubjectSemesters(5L, listOf(7L, 8L)) } returns Unit

        val result = UpdateSubjectUseCase(repo)(
            Subject(5L, "  OOP   Advanced  ", "  OOP   ADV "),
            listOf(7L, 8L)
        )

        assertEquals(EntityWriteResult.SUCCESS, result)
        coVerify {
            repo.updateSubject(match {
                it.id == 5L && it.name == "OOP Advanced" && it.abbreviation == "OOP ADV"
            })
        }
        coVerify { repo.replaceSubjectSemesters(5L, listOf(7L, 8L)) }
    }
}
