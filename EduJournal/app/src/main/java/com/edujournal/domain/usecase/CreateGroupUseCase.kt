package com.edujournal.domain.usecase

import com.edujournal.domain.model.Group
import com.edujournal.domain.repository.GroupRepository

class CreateGroupUseCase(
    private val repository: GroupRepository
){
    suspend operator fun invoke(name: String) {
        val group = Group(
            id = 0,
            name = name
        )
        repository.createGroup(group)
    }
}