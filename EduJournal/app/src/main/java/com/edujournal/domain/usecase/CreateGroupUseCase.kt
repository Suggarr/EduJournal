package com.edujournal.domain.usecase

import com.edujournal.domain.model.Group
import com.edujournal.domain.repository.GroupRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val repository: GroupRepository
){
    suspend operator fun invoke(name: String): EntityWriteResult {
        if (repository.existsByName(name)) return EntityWriteResult.DUPLICATE
        val group = Group(
            id = 0,
            name = name
        )
        repository.createGroup(group)
        return EntityWriteResult.SUCCESS
    }
}
