package com.edujournal.domain.usecase

import com.edujournal.domain.model.Group
import com.edujournal.domain.repository.GroupRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
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