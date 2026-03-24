package com.edujournal.domain.usecase

import com.edujournal.domain.model.Group
import com.edujournal.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupsUseCase @Inject constructor(
    private val repository : GroupRepository)
{
    operator fun invoke(): Flow<List<Group>> {
        return repository.getGroups()
    }
}