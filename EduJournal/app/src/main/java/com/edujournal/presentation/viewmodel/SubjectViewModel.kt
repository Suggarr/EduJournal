// D:/DiplomProject/EduJournal/app/src/main/java/com/edujournal/presentation/viewmodel/SubjectViewModel.kt
package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.domain.model.Subject
import com.edujournal.domain.usecase.CreateSubjectUseCase
import com.edujournal.domain.usecase.ObserveSubjectsUseCase
import com.edujournal.domain.usecase.UpdateSubjectUseCase
import com.edujournal.domain.usecase.DeleteSubjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val observeSubjectsUseCase: ObserveSubjectsUseCase,
    private val createSubjectUseCase: CreateSubjectUseCase,
    private val updateSubjectUseCase: UpdateSubjectUseCase,
    private val deleteSubjectUseCase: DeleteSubjectUseCase
) : ViewModel() {

    val subjects: StateFlow<List<Subject>> = observeSubjectsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Теперь принимаем и описание
    fun addSubject(name: String, description: String?) {
        viewModelScope.launch {
            createSubjectUseCase(name, description)
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            updateSubjectUseCase(subject)
        }
    }

    fun deleteSubject(subjectId: Long) {
        viewModelScope.launch {
            deleteSubjectUseCase(subjectId)
        }
    }
}