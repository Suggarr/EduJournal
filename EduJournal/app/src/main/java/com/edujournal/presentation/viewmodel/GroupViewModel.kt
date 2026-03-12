package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.Group
import com.edujournal.domain.usecase.CreateGroupUseCase
import com.edujournal.domain.usecase.GetGroupsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase,
    private val getGroupsUseCase: GetGroupsUseCase
) : ViewModel(){

    val groups: StateFlow<List<Group>> =
        getGroupsUseCase()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun addGroup(name: String){
        viewModelScope.launch{
            createGroupUseCase(name)
        }
    }
}