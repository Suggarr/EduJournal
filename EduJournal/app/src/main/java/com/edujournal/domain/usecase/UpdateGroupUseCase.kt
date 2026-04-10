package com.edujournal.domain.usecase

import com.edujournal.domain.model.Group
import com.edujournal.domain.repository.GroupRepository
import javax.inject.Inject

class UpdateGroupUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(group: Group): EntityWriteResult {
        if (!repository.existsById(group.id)) return EntityWriteResult.NOT_FOUND
        if (repository.existsByNameExceptId(group.name, group.id)) return EntityWriteResult.DUPLICATE
        repository.updateGroup(group)
        return EntityWriteResult.SUCCESS
    }
}
