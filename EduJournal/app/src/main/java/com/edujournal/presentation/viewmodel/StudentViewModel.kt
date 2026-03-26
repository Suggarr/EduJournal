package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.Student
import com.edujournal.domain.usecase.CreateStudentUseCase
import com.edujournal.domain.usecase.DeleteStudentUseCase
import com.edujournal.domain.usecase.GetGroupsUseCase
import com.edujournal.domain.usecase.ObserveStudentsUseCase
import com.edujournal.domain.usecase.UpdateStudentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val observeStudentsUseCase: ObserveStudentsUseCase,
    private val createStudentUseCase: CreateStudentUseCase,
    private val updateStudentUseCase: UpdateStudentUseCase,
    private val deleteStudentUseCase: DeleteStudentUseCase,
    private val getGroupsUseCase: GetGroupsUseCase
) : ViewModel() {

    private val _groupId = MutableStateFlow<Long?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val students: StateFlow<List<Student>> = _groupId
        .filterNotNull()
        .flatMapLatest { id -> observeStudentsUseCase(id) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val groupName: StateFlow<String?> = combine(
        _groupId,
        getGroupsUseCase()
    ) { currentGroupId, groups ->
        groups.firstOrNull { it.id == currentGroupId }?.name
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    fun load(groupId: Long) {
        _groupId.value = groupId
    }

    fun addStudent(firstName: String, lastName: String, middleName: String, groupId: Long) {
        viewModelScope.launch {
            createStudentUseCase(firstName, lastName, middleName, groupId)
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            updateStudentUseCase(student)
        }
    }

    fun deleteStudent(studentId: Long) {
        viewModelScope.launch {
            deleteStudentUseCase(studentId)
        }
    }
}
