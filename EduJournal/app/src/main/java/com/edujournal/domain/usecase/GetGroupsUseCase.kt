package com.edujournal.domain.usecase

import com.edujournal.domain.repository.GroupRepository

class GetGroupsUseCase (
    private val repository : GroupRepository)
{
    suspend operator fun invoke() = repository.getGroups()
}