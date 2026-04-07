package com.edujournal.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.domain.model.Lesson
import com.edujournal.presentation.viewmodel.LessonTopicsViewModel
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val lessonDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonTopicsScreen(
    groupId: Long,
    subjectId: Long,
    lessonTypeId: Long,
    semesterId: Long,
    onBack: () -> Unit,
    viewModel: LessonTopicsViewModel = hiltViewModel()
) {
    val lessonsFlow = remember(groupId, subjectId, lessonTypeId, semesterId) {
        viewModel.observeLessons(groupId, subjectId, lessonTypeId, semesterId)
    }
    val lessons by lessonsFlow.collectAsState()
    val currentLessons = lessons
    val requiredHours by viewModel.observeRequiredHours(subjectId, lessonTypeId).collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var lessonToEdit by remember { mutableStateOf<Lesson?>(null) }
    var lessonToDelete by remember { mutableStateOf<Lesson?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.lesson_topics_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.common_add))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            requiredHours?.let { hours ->
                Text(
                    text = stringResource(
                        R.string.lesson_topics_required_hours,
                        formatHoursValue(hours)
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                )
            }

            if (currentLessons == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (currentLessons.isEmpty()) {
                Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.lesson_topics_empty))
            }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(0.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(currentLessons) { lesson ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lesson.date.format(lessonDateFormatter),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = lesson.topic)
                                }
                                IconButton(onClick = { lessonToEdit = lesson }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = stringResource(R.string.common_edit)
                                    )
                                }
                                IconButton(onClick = { lessonToDelete = lesson }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.common_delete),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        LessonTopicDialog(
            title = stringResource(R.string.lesson_topics_add),
            isDateBusy = { selectedDate ->
                currentLessons.orEmpty().any { it.date == selectedDate }
            },
            onDismiss = { showAddDialog = false },
            onConfirm = { date, topic ->
                viewModel.addLesson(groupId, subjectId, lessonTypeId, semesterId, date, topic)
                showAddDialog = false
            }
        )
    }

    lessonToEdit?.let { lesson ->
        LessonTopicDialog(
            title = stringResource(R.string.lesson_topics_edit),
            initialDate = lesson.date,
            initialTopic = lesson.topic,
            isDateBusy = { selectedDate ->
                currentLessons.orEmpty().any { it.id != lesson.id && it.date == selectedDate }
            },
            onDismiss = { lessonToEdit = null },
            onConfirm = { date, topic ->
                viewModel.updateLesson(lesson.copy(date = date, topic = topic))
                lessonToEdit = null
            }
        )
    }

    lessonToDelete?.let { lesson ->
        AlertDialog(
            onDismissRequest = { lessonToDelete = null },
            title = { Text(stringResource(R.string.lesson_topics_delete_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.lesson_topics_delete_message,
                        lesson.date.format(lessonDateFormatter)
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteLesson(lesson.id)
                        lessonToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.common_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { lessonToDelete = null }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}

private fun formatHoursValue(hours: Double): String {
    return BigDecimal.valueOf(hours)
        .stripTrailingZeros()
        .toPlainString()
        .replace('.', ',')
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonTopicDialog(
    title: String,
    initialDate: LocalDate = LocalDate.now(),
    initialTopic: String = "",
    isDateBusy: (LocalDate) -> Boolean = { false },
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, String) -> Unit
) {
    var topic by remember { mutableStateOf(initialTopic) }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateBusy = isDateBusy(selectedDate)
    val canSave = topic.isNotBlank() && !dateBusy

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.lesson_topics_selected_date,
                            selectedDate.format(lessonDateFormatter)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { showDatePicker = true }) {
                        Text(stringResource(R.string.lesson_topics_pick_date))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text(stringResource(R.string.lesson_topics_topic_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (dateBusy) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.lesson_topics_duplicate_date_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedDate, topic.trim()) },
                enabled = canSave
            ) {
                Text(stringResource(R.string.common_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    )

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        val millis = pickerState.selectedDateMillis
                        if (millis != null) {
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.common_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}
