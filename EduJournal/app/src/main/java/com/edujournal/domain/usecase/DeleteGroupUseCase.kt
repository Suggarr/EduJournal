package com.edujournal.domain.usecase

import com.edujournal.domain.repository.GroupRepository
import javax.inject.Inject

class DeleteGroupUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteGroup(id)
    }
}