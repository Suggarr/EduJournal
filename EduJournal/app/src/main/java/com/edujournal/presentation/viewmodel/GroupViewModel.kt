package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.R
import com.edujournal.domain.model.Group
import com.edujournal.domain.usecase.CreateGroupUseCase
import com.edujournal.domain.usecase.DeleteGroupUseCase
import com.edujournal.domain.usecase.EntityWriteResult
import com.edujournal.domain.usecase.GetGroupsUseCase
import com.edujournal.domain.usecase.UpdateGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase
) : ViewModel(){

    private val _uiMessageRes = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val uiMessageRes: SharedFlow<Int> = _uiMessageRes.asSharedFlow()

    val groups: StateFlow<List<Group>> =
        getGroupsUseCase()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun addGroup(name: String, onResult: (EntityWriteResult) -> Unit = {}) {
        viewModelScope.launch {
            val result = createGroupUseCase(name)
            when (result) {
                EntityWriteResult.DUPLICATE -> _uiMessageRes.emit(R.string.group_duplicate_error)
                EntityWriteResult.NOT_FOUND -> _uiMessageRes.emit(R.string.group_not_found_error)
                EntityWriteResult.SUCCESS -> Unit
            }
            onResult(result)
        }
    }

    fun updateGroup(group: Group, onResult: (EntityWriteResult) -> Unit = {}) {
        viewModelScope.launch {
            val result = updateGroupUseCase(group)
            when (result) {
                EntityWriteResult.DUPLICATE -> _uiMessageRes.emit(R.string.group_duplicate_error)
                EntityWriteResult.NOT_FOUND -> _uiMessageRes.emit(R.string.group_not_found_error)
                EntityWriteResult.SUCCESS -> Unit
            }
            onResult(result)
        }
    }

    fun deleteGroup(id: Long) {
        viewModelScope.launch {
            deleteGroupUseCase(id)
        }
    }
}
