package com.edujournal.domain.usecase.group
import com.edujournal.domain.usecase.common.EntityWriteResult

import com.edujournal.domain.model.Group
import com.edujournal.domain.repository.GroupRepository
import com.edujournal.utils.normalizeSpaces
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val repository: GroupRepository
){
    suspend operator fun invoke(name: String): EntityWriteResult {
        val normalizedName = name.normalizeSpaces()
        if (repository.existsByName(normalizedName)) return EntityWriteResult.DUPLICATE
        val group = Group(
            id = 0,
            name = normalizedName
        )
        repository.createGroup(group)
        return EntityWriteResult.SUCCESS
    }
}




