package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.R
import com.edujournal.domain.model.Student
import com.edujournal.domain.usecase.CreateStudentUseCase
import com.edujournal.domain.usecase.DeleteStudentUseCase
import com.edujournal.domain.usecase.GetGroupsUseCase
import com.edujournal.domain.usecase.ObserveStudentsUseCase
import com.edujournal.domain.usecase.UpdateStudentUseCase
import com.edujournal.presentation.studentimport.ImportStudentRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    fun importStudents(
        groupId: Long,
        importedStudents: List<ImportStudentRow>,
        onCompleted: (added: Int, skipped: Int) -> Unit
    ) {
        viewModelScope.launch {
            val existingKeys = students.value
                .map { student ->
                    studentKey(
                        lastName = student.lastName,
                        firstName = student.firstName,
                        middleName = student.middleName
                    )
                }
                .toMutableSet()

            var added = 0
            var skipped = 0

            importedStudents.forEach { student ->
                val key = studentKey(
                    lastName = student.lastName,
                    firstName = student.firstName,
                    middleName = student.middleName
                )

                if (key in existingKeys) {
                    skipped++
                    return@forEach
                }

                createStudentUseCase(
                    firstName = student.firstName,
                    lastName = student.lastName,
                    middleName = student.middleName,
                    groupId = groupId
                )
                existingKeys += key
                added++
            }

            onCompleted(added, skipped)
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

    private fun studentKey(lastName: String, firstName: String, middleName: String): String {
        return listOf(lastName, firstName, middleName)
            .joinToString("|") { it.trim().lowercase() }
    }
}
