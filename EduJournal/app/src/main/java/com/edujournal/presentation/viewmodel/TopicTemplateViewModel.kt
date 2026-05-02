package com.edujournal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edujournal.R
import com.edujournal.domain.model.TopicTemplate
import com.edujournal.domain.usecase.topictemplate.CreateTopicTemplateUseCase
import com.edujournal.domain.usecase.topictemplate.DeleteTopicTemplateUseCase
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.domain.usecase.topictemplate.ObserveTopicTemplatesUseCase
import com.edujournal.domain.usecase.topictemplate.UpdateTopicTemplateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicTemplateViewModel @Inject constructor(
    private val observeTopicTemplatesUseCase: ObserveTopicTemplatesUseCase,
    private val createTopicTemplateUseCase: CreateTopicTemplateUseCase,
    private val updateTopicTemplateUseCase: UpdateTopicTemplateUseCase,
    private val deleteTopicTemplateUseCase: DeleteTopicTemplateUseCase
) : ViewModel() {

    private val _uiMessageRes = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val uiMessageRes: SharedFlow<Int> = _uiMessageRes.asSharedFlow()

    private val _context = MutableStateFlow<Pair<Long, Long>?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val templates: StateFlow<List<TopicTemplate>> = _context
        .filterNotNull()
        .flatMapLatest { (semesterId, subjectLessonTypeId) ->
            observeTopicTemplatesUseCase(semesterId, subjectLessonTypeId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun load(semesterId: Long, subjectLessonTypeId: Long) {
        _context.value = semesterId to subjectLessonTypeId
    }

    fun addTemplate(title: String, onResult: (EntityWriteResult) -> Unit = {}) {
        viewModelScope.launch {
            val (semesterId, subjectLessonTypeId) = _context.value ?: return@launch
            val nextOrder = (templates.value.maxOfOrNull { it.orderInType } ?: 0) + 1
            val result = createTopicTemplateUseCase(
                TopicTemplate(
                    id = 0,
                    semesterId = semesterId,
                    subjectLessonTypeId = subjectLessonTypeId,
                    title = title,
                    orderInType = nextOrder
                )
            )
            if (result == EntityWriteResult.DUPLICATE) {
                _uiMessageRes.emit(R.string.topic_template_duplicate_error)
            }
            onResult(result)
        }
    }

    fun addTemplatesBatch(raw: String, onDone: (added: Int) -> Unit = {}) {
        viewModelScope.launch {
            val (semesterId, subjectLessonTypeId) = _context.value ?: return@launch
            val lines = raw.lineSequence()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .toList()
            if (lines.isEmpty()) {
                onDone(0)
                return@launch
            }

            var nextOrder = (templates.value.maxOfOrNull { it.orderInType } ?: 0) + 1
            var added = 0
            lines.forEach { title ->
                val result = createTopicTemplateUseCase(
                    TopicTemplate(
                        id = 0,
                        semesterId = semesterId,
                        subjectLessonTypeId = subjectLessonTypeId,
                        title = title,
                        orderInType = nextOrder
                    )
                )
                if (result == EntityWriteResult.SUCCESS) {
                    nextOrder++
                    added++
                }
            }
            onDone(added)
        }
    }

    fun updateTemplate(template: TopicTemplate, newTitle: String, onResult: (EntityWriteResult) -> Unit = {}) {
        viewModelScope.launch {
            val result = updateTopicTemplateUseCase(template.copy(title = newTitle))
            if (result == EntityWriteResult.DUPLICATE) {
                _uiMessageRes.emit(R.string.topic_template_duplicate_error)
            }
            onResult(result)
        }
    }

    fun deleteTemplate(id: Long) {
        viewModelScope.launch { deleteTopicTemplateUseCase(id) }
    }
}


