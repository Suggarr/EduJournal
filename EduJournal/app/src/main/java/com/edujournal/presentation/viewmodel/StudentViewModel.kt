package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.Student
import com.edujournal.domain.usecase.CreateStudentUseCase
import com.edujournal.domain.usecase.ObserveStudentsUseCase
import com.edujournal.domain.repository.StudentRepository
import com.edujournal.domain.usecase.DeleteStudentUseCase
import com.edujournal.domain.usecase.UpdateStudentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val observeStudentsUseCase: ObserveStudentsUseCase,
    private val createStudentUseCase: CreateStudentUseCase,
    private val updateStudentUseCase: UpdateStudentUseCase,
    private val deleteStudentUseCase: DeleteStudentUseCase
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