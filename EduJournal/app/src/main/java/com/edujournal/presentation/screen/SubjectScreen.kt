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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.domain.model.LessonType
import com.edujournal.domain.model.Subject
import com.edujournal.presentation.viewmodel.SubjectViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    userName: String,
    onSubjectClick: (Long) -> Unit,
    viewModel: SubjectViewModel = hiltViewModel()
) {
    val subjects by viewModel.subjects.collectAsState()
    val lessonTypes by viewModel.lessonTypes.collectAsState()
    val subjectHoursBySubjectId by viewModel.subjectHoursBySubjectId.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.subject_greeting, userName),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.subject_title),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
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
        Box(modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp)) {
            if (subjects.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.subject_empty), color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subjects) { subject ->
                        SubjectCard(
                            subject = subject,
                            onClick = { onSubjectClick(subject.id) },
                            onEditClick = { subjectToEdit = subject },
                            onDeleteClick = { subjectToDelete = subject }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        SubjectDialog(
            title = stringResource(R.string.subject_new),
            lessonTypes = lessonTypes,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, abbreviation, lessonTypeHours ->
                viewModel.addSubject(name, abbreviation, lessonTypeHours)
                showAddDialog = false
            }
        )
    }

    subjectToEdit?.let { subject ->
        SubjectDialog(
            title = stringResource(R.string.subject_edit),
            lessonTypes = lessonTypes,
            initialName = subject.name,
            initialAbbreviation = subject.abbreviation ?: "",
            initialHoursByLessonTypeId = subjectHoursBySubjectId[subject.id].orEmpty(),
            onDismiss = { subjectToEdit = null },
            onConfirm = { newName, newAbbreviation, lessonTypeHours ->
                viewModel.updateSubject(
                    subject = subject.copy(name = newName, abbreviation = newAbbreviation),
                    lessonTypeHours = lessonTypeHours
                )
                subjectToEdit = null
            }
        )
    }

    subjectToDelete?.let { subject ->
        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = { Text(stringResource(R.string.subject_delete_title)) },
            text = { Text(stringResource(R.string.subject_delete_message, subject.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSubject(subject.id)
                        subjectToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.common_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { subjectToDelete = null }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}

@Composable
fun SubjectCard(
    subject: Subject,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleLarge
                )
                subject.abbreviation?.let {
                    if (it.isNotBlank()) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.common_edit),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.common_delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun SubjectDialog(
    title: String,
    lessonTypes: List<LessonType>,
    initialName: String = "",
    initialAbbreviation: String = "",
    initialHoursByLessonTypeId: Map<Long, Double?> = emptyMap(),
    onDismiss: () -> Unit,
    onConfirm: (String, String?, Map<Long, Double?>) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var abbreviation by remember { mutableStateOf(initialAbbreviation) }
    var showHoursDialog by remember { mutableStateOf(false) }

    val hourInputs = remember(lessonTypes, initialHoursByLessonTypeId) {
        mutableStateMapOf<Long, String>().apply {
            lessonTypes.forEach { lessonType ->
                this[lessonType.id] = formatHoursInput(initialHoursByLessonTypeId[lessonType.id])
            }
        }
    }

    val filledHoursCount = lessonTypes.count { !hourInputs[it.id].isNullOrBlank() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.subject_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = abbreviation,
                    onValueChange = { abbreviation = it },
                    label = { Text(stringResource(R.string.subject_abbreviation_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showHoursDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(
                            R.string.subject_hours_button,
                            filledHoursCount,
                            lessonTypes.size
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val lessonTypeHours = lessonTypes.associate { lessonType ->
                        val raw = hourInputs[lessonType.id].orEmpty().trim()
                        val parsed = raw.replace(',', '.').toDoubleOrNull()
                        lessonType.id to parsed
                    }
                    onConfirm(name, abbreviation.ifBlank { null }, lessonTypeHours)
                },
                enabled = name.isNotBlank()
            ) { Text(stringResource(R.string.common_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) }
        }
    )

    if (showHoursDialog) {
        SubjectLessonTypeHoursDialog(
            lessonTypes = lessonTypes,
            currentHourInputs = hourInputs,
            onDismiss = { showHoursDialog = false },
            onSave = { updated ->
                hourInputs.clear()
                hourInputs.putAll(updated)
                showHoursDialog = false
            }
        )
    }
}

private fun formatHoursInput(hours: Double?): String {
    if (hours == null) return ""
    return BigDecimal.valueOf(hours)
        .stripTrailingZeros()
        .toPlainString()
        .replace('.', ',')
}

@Composable
private fun SubjectLessonTypeHoursDialog(
    lessonTypes: List<LessonType>,
    currentHourInputs: Map<Long, String>,
    onDismiss: () -> Unit,
    onSave: (Map<Long, String>) -> Unit
) {
    val draftInputs = remember(lessonTypes, currentHourInputs) {
        mutableStateMapOf<Long, String>().apply {
            lessonTypes.forEach { lessonType ->
                this[lessonType.id] = currentHourInputs[lessonType.id].orEmpty()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.subject_hours_title)) },
        text = {
            if (lessonTypes.isEmpty()) {
                Text(stringResource(R.string.lesson_type_empty))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(lessonTypes) { lessonType ->
                        OutlinedTextField(
                            value = draftInputs[lessonType.id].orEmpty(),
                            onValueChange = { value ->
                                val normalized = value.replace('.', ',')
                                val filtered = buildString {
                                    var hasSeparator = false
                                    for (ch in normalized) {
                                        when {
                                            ch.isDigit() -> append(ch)
                                            ch == ',' && !hasSeparator -> {
                                                append(ch)
                                                hasSeparator = true
                                            }
                                        }
                                    }
                                }
                                draftInputs[lessonType.id] = filtered
                            },
                            label = {
                                Text(stringResource(R.string.subject_hours_field_label, lessonType.name))
                            },
                            placeholder = {
                                Text(stringResource(R.string.subject_hours_optional))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(draftInputs.toMap()) }) {
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
