package com.edujournal.domain.usecase

import com.edujournal.domain.model.Group
import com.edujournal.domain.repository.GroupRepository
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.group.CreateGroupUseCase
import com.edujournal.domain.usecase.group.UpdateGroupUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GroupUseCasesTest {
    @Test
    fun `create group returns success when inserted`() = runBlocking {
        val repo = mockk<GroupRepository>()
        coEvery { repo.createGroup(any()) } returns 10L
        val result = CreateGroupUseCase(repo)("PI-101")
        assertEquals(EntityWriteResult.SUCCESS, result)
    }

    @Test
    fun `create group returns duplicate on insert ignore`() = runBlocking {
        val repo = mockk<GroupRepository>()
        coEvery { repo.createGroup(any()) } returns -1L
        val result = CreateGroupUseCase(repo)("PI-101")
        assertEquals(EntityWriteResult.DUPLICATE, result)
    }

    @Test
    fun `update group returns success when row updated`() = runBlocking {
        val repo = mockk<GroupRepository>()
        coEvery { repo.updateGroup(any()) } returns 1
        val result = UpdateGroupUseCase(repo)(Group(7L, "PI-101"))
        assertEquals(EntityWriteResult.SUCCESS, result)
    }

    @Test
    fun `update group returns not found when no rows updated`() = runBlocking {
        val repo = mockk<GroupRepository>()
        coEvery { repo.updateGroup(any()) } returns 0
        val result = UpdateGroupUseCase(repo)(Group(7L, "PI-101"))
        assertEquals(EntityWriteResult.NOT_FOUND, result)
        coVerify(exactly = 1) { repo.updateGroup(any()) }
    }
}
