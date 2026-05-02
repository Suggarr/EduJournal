package com.edujournal.domain.usecase.group
import com.edujournal.domain.usecase.common.EntityWriteResult

import com.edujournal.domain.model.Group
import com.edujournal.domain.repository.GroupRepository
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class UpdateGroupUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(group: Group): EntityWriteResult {
        val normalizedGroup = group.copy(name = group.name.normalizeSpaces())
        if (!repository.existsById(normalizedGroup.id)) return EntityWriteResult.NOT_FOUND
        if (repository.existsByNameExceptId(normalizedGroup.name, normalizedGroup.id)) return EntityWriteResult.DUPLICATE
        repository.updateGroup(normalizedGroup)
        return EntityWriteResult.SUCCESS
    }
}




