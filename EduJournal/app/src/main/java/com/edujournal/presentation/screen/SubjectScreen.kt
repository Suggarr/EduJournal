package com.edujournal.presentation.screen

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.model.Semester
import com.edujournal.domain.model.SemesterSeason
import com.edujournal.domain.model.Subject
import com.edujournal.domain.usecase.EntityWriteResult
import com.edujournal.presentation.viewmodel.SubjectViewModel
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    userName: String,
    semesters: List<Semester>,
    selectedSemesterId: Long?,
    onSemesterSelected: (Long) -> Unit,
    onSubjectClick: (Long) -> Unit,
    viewModel: SubjectViewModel = hiltViewModel()
) {
    val subjects by viewModel.subjects.collectAsState()
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }
    var semesterMenuExpanded by remember { mutableStateOf(false) }
    val autumnLabel = stringResource(R.string.settings_semester_autumn)
    val springLabel = stringResource(R.string.settings_semester_spring)
    val selectedSemesterName = semesters
        .firstOrNull { it.id == selectedSemesterId }
        ?.let { semester ->
            val season = if (semester.season == SemesterSeason.AUTUMN) autumnLabel else springLabel
            "$season ${semester.year}"
        }

    LaunchedEffect(viewModel) {
        viewModel.uiMessageRes.collect { messageRes ->
            Toast.makeText(context, context.getString(messageRes), Toast.LENGTH_SHORT).show()
        }
    }

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
        Column(modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp)) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                OutlinedButton(
                    onClick = { semesterMenuExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val label = selectedSemesterName ?: stringResource(R.string.semester_select)
                    Text("${stringResource(R.string.semester_label)}: $label")
                }
                DropdownMenu(
                    expanded = semesterMenuExpanded,
                    onDismissRequest = { semesterMenuExpanded = false }
                ) {
                    semesters.forEach { semester ->
                        DropdownMenuItem(
                            text = {
                                val season = if (semester.season == SemesterSeason.AUTUMN) autumnLabel else springLabel
                                Text("$season ${semester.year}")
                            },
                            onClick = {
                                semesterMenuExpanded = false
                                onSemesterSelected(semester.id)
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
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
    }

    if (showAddDialog) {
        SubjectDialog(
            title = stringResource(R.string.subject_new),
            onDismiss = { showAddDialog = false },
            onConfirm = { name, abbreviation ->
                viewModel.addSubject(name, abbreviation) { result ->
                    if (result != EntityWriteResult.DUPLICATE) {
                        showAddDialog = false
                    }
                }
            }
        )
    }

    subjectToEdit?.let { subject ->
        SubjectDialog(
            title = stringResource(R.string.subject_edit),
            initialName = subject.name,
            initialAbbreviation = subject.abbreviation ?: "",
            onDismiss = { subjectToEdit = null },
            onConfirm = { newName, newAbbreviation ->
                viewModel.updateSubject(
                    subject = subject.copy(name = newName, abbreviation = newAbbreviation)
                ) { result ->
                    if (result != EntityWriteResult.DUPLICATE) {
                        subjectToEdit = null
                    }
                }
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
    initialName: String = "",
    initialAbbreviation: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var abbreviation by remember { mutableStateOf(initialAbbreviation) }

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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(name, abbreviation.ifBlank { null })
                },
                enabled = name.isNotBlank()
            ) { Text(stringResource(R.string.common_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) }
        }
    )
}

