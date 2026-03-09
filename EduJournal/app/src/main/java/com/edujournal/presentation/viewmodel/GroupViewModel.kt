package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.Group
import com.edujournal.domain.usecase.CreateGroupUseCase
import com.edujournal.domain.usecase.GetGroupsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase,
    private val getGroupsUseCase: GetGroupsUseCase
) : ViewModel(){

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups

    init {
        loadGroups()
    }
    fun loadGroups(){
        viewModelScope.launch{
            _groups.value = getGroupsUseCase()
        }
    }

    fun addGroup(name: String){
        viewModelScope.launch{
            createGroupUseCase(name)
            loadGroups()
        }
    }
}