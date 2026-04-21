package com.edujournal.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.domain.model.Homework
import com.edujournal.presentation.component.ScrollAwareAddFab
import com.edujournal.presentation.viewmodel.HomeworkDisplayStatus
import com.edujournal.presentation.viewmodel.HomeworkStudentUi
import com.edujournal.presentation.viewmodel.HomeworkViewModel
import java.time.format.DateTimeFormatter

private val homeworkDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkScreen(
    lessonId: Long,
    onBack: () -> Unit,
    viewModel: HomeworkViewModel = hiltViewModel()
) {
    val stateFlow = remember(lessonId) {
        viewModel.observeState(lessonId)
    }
    val state by stateFlow.collectAsState()
    val lessonDateInTitle = state?.lessonDate?.format(homeworkDateFormatter)
    var showAddDialog by remember { mutableStateOf(false) }
    var homeworkToEdit by remember { mutableStateOf<Homework?>(null) }
    var homeworkToDelete by remember { mutableStateOf<Homework?>(null) }
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = lessonDateInTitle?.let {
                            stringResource(R.string.homework_title_for_lesson, it)
                        } ?: stringResource(R.string.homework_title_short)
                    )
                },
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
            ScrollAwareAddFab(
                listState = listState,
                onClick = { showAddDialog = true },
                contentDescription = stringResource(R.string.common_add)
            )
        }
    ) { paddingValues ->
        val ui = state
        if (ui == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            val homework = ui.homework
            if (homework == null) {
                item {
                    Text(
                        text = stringResource(R.string.homework_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                item {
                    HomeworkCard(
                        homework = homework,
                        students = ui.students,
                        onEditHomework = { homeworkToEdit = it },
                        onDeleteHomework = { homeworkToDelete = it },
                        onSetSubmitted = { student ->
                            viewModel.setStudentSubmitted(
                                homeworkId = homework.id,
                                studentId = student.studentId
                            )
                        },
                        onSetNotSubmitted = { student ->
                            viewModel.setStudentNotSubmitted(
                                homeworkId = homework.id,
                                studentId = student.studentId
                            )
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        HomeworkDialog(
            title = stringResource(R.string.homework_add),
            onDismiss = { showAddDialog = false },
            onConfirm = { text ->
                viewModel.addHomework(
                    lessonId = lessonId,
                    text = text
                )
                showAddDialog = false
            }
        )
    }

    homeworkToEdit?.let { homework ->
        HomeworkDialog(
            title = stringResource(R.string.homework_edit),
            initialText = homework.text,
            onDismiss = { homeworkToEdit = null },
            onConfirm = { text ->
                viewModel.updateHomework(
                    homework.copy(
                        text = text
                    )
                )
                homeworkToEdit = null
            }
        )
    }

    homeworkToDelete?.let { homework ->
        AlertDialog(
            onDismissRequest = { homeworkToDelete = null },
            title = { Text(stringResource(R.string.homework_delete_title)) },
            text = { Text(stringResource(R.string.homework_delete_message, homework.text)) },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteHomework(homework.id)
                    homeworkToDelete = null
                }) {
                    Text(stringResource(R.string.common_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { homeworkToDelete = null }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}

@Composable
private fun HomeworkCard(
    homework: Homework,
    students: List<HomeworkStudentUi>,
    onEditHomework: (Homework) -> Unit,
    onDeleteHomework: (Homework) -> Unit,
    onSetSubmitted: (HomeworkStudentUi) -> Unit,
    onSetNotSubmitted: (HomeworkStudentUi) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = homework.text,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                IconButton(onClick = { onEditHomework(homework) }) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.common_edit))
                }
                IconButton(onClick = { onDeleteHomework(homework) }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.common_delete))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            students.forEach { student ->
                HomeworkStudentRow(
                    student = student,
                    onSetSubmitted = { onSetSubmitted(student) },
                    onSetNotSubmitted = { onSetNotSubmitted(student) }
                )
            }
        }
    }
}

@Composable
private fun HomeworkStudentRow(
    student: HomeworkStudentUi,
    onSetSubmitted: () -> Unit,
    onSetNotSubmitted: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = student.studentName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusChip(status = student.displayStatus, modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onSetSubmitted,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(stringResource(R.string.homework_status_submitted_short))
            }
            OutlinedButton(
                onClick = onSetNotSubmitted,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(stringResource(R.string.homework_status_not_submitted_short))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun StatusChip(
    status: HomeworkDisplayStatus,
    modifier: Modifier = Modifier
) {
    val (textRes, color) = when (status) {
        HomeworkDisplayStatus.SUBMITTED -> {
            R.string.homework_status_submitted to MaterialTheme.colorScheme.primary
        }
        HomeworkDisplayStatus.NOT_SUBMITTED -> {
            R.string.homework_status_not_submitted to MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

    Text(
        text = stringResource(textRes),
        color = color,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeworkDialog(
    title: String,
    initialText: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.homework_text_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text.trim()) },
                enabled = text.isNotBlank()
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
}
