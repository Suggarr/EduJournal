package com.edujournal.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.R
import com.edujournal.domain.model.Student
import com.edujournal.domain.usecase.student.CreateStudentUseCase
import com.edujournal.domain.usecase.student.DeleteStudentUseCase
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.group.GetGroupsUseCase
import com.edujournal.domain.usecase.student.ObserveStudentsUseCase
import com.edujournal.domain.usecase.student.UpdateStudentUseCase
import com.edujournal.presentation.studentimport.ImportStudentRow
import com.edujournal.presentation.studentimport.StudentImportManager
import com.edujournal.presentation.studentimport.StudentImportParseResult
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

sealed interface StudentImportEvent {
    data object Empty : StudentImportEvent
    data class ParseError(val reason: String) : StudentImportEvent
    data class Result(val added: Int, val skipped: Int) : StudentImportEvent
}

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val observeStudentsUseCase: ObserveStudentsUseCase,
    private val createStudentUseCase: CreateStudentUseCase,
    private val updateStudentUseCase: UpdateStudentUseCase,
    private val deleteStudentUseCase: DeleteStudentUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val studentImportManager: StudentImportManager
) : ViewModel() {

    private val _groupId = MutableStateFlow<Long?>(null)
    private val _uiMessageRes = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val uiMessageRes: SharedFlow<Int> = _uiMessageRes.asSharedFlow()
    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting
    private val _importEvents = MutableSharedFlow<StudentImportEvent>(extraBufferCapacity = 1)
    val importEvents: SharedFlow<StudentImportEvent> = _importEvents.asSharedFlow()
    val supportedImportMimeTypes: Array<String> = studentImportManager.supportedMimeTypes

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

    fun addStudent(
        firstName: String,
        lastName: String,
        middleName: String?,
        groupId: Long,
        onResult: (EntityWriteResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = createStudentUseCase(firstName, lastName, middleName, groupId)
            when (result) {
                EntityWriteResult.DUPLICATE -> _uiMessageRes.emit(R.string.student_duplicate_error)
                EntityWriteResult.NOT_FOUND -> _uiMessageRes.emit(R.string.student_not_found_error)
                EntityWriteResult.SUCCESS -> Unit
            }
            onResult(result)
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

                when (
                    createStudentUseCase(
                        firstName = student.firstName,
                        lastName = student.lastName,
                        middleName = student.middleName,
                        groupId = groupId
                    )
                ) {
                    EntityWriteResult.SUCCESS -> {
                        existingKeys += key
                        added++
                    }
                    EntityWriteResult.DUPLICATE -> {
                        skipped++
                    }
                    EntityWriteResult.NOT_FOUND -> {
                        skipped++
                    }
                }
            }

            onCompleted(added, skipped)
        }
    }

    fun importStudentsFromFile(
        groupId: Long,
        uri: Uri
    ) {
        viewModelScope.launch {
            _isImporting.value = true
            when (val parseResult = studentImportManager.parse(uri)) {
                is StudentImportParseResult.Success -> {
                    if (parseResult.students.isEmpty()) {
                        _isImporting.value = false
                        _importEvents.emit(StudentImportEvent.Empty)
                        return@launch
                    }
                    importStudents(groupId, parseResult.students) { added, skipped ->
                        viewModelScope.launch {
                            _isImporting.value = false
                            _importEvents.emit(StudentImportEvent.Result(added, skipped))
                        }
                    }
                }

                is StudentImportParseResult.Error -> {
                    _isImporting.value = false
                    _importEvents.emit(StudentImportEvent.ParseError(parseResult.reason))
                }
            }
        }
    }

    fun updateStudent(student: Student, onResult: (EntityWriteResult) -> Unit = {}) {
        viewModelScope.launch {
            val result = updateStudentUseCase(student)
            when (result) {
                EntityWriteResult.DUPLICATE -> _uiMessageRes.emit(R.string.student_duplicate_error)
                EntityWriteResult.NOT_FOUND -> _uiMessageRes.emit(R.string.student_not_found_error)
                EntityWriteResult.SUCCESS -> Unit
            }
            onResult(result)
        }
    }

    fun deleteStudent(studentId: Long) {
        viewModelScope.launch {
            deleteStudentUseCase(studentId)
        }
    }

    private fun studentKey(lastName: String, firstName: String, middleName: String?): String {
        return listOf(lastName, firstName, middleName)
            .joinToString("|") { it.orEmpty().trim().lowercase() }
    }
}
