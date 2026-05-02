package com.edujournal.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.domain.model.Semester
import com.edujournal.domain.model.enum.SemesterSeason
import com.edujournal.domain.model.Subject
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.presentation.component.ScrollAwareAddFab
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
    val listState = rememberLazyListState()
    var editSubjectSemesterIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var yearMenuExpanded by remember { mutableStateOf(false) }
    var semesterMenuExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val autumnLabel = stringResource(R.string.settings_semester_autumn)
    val springLabel = stringResource(R.string.settings_semester_spring)
    val selectedSemester = semesters.firstOrNull { it.id == selectedSemesterId }
    val selectedYear = selectedSemester?.year
    val availableYears = semesters.map { it.year }.distinct().sorted()
    val semestersOfSelectedYear = if (selectedYear == null) {
        emptyList()
    } else {
        semesters.filter { it.year == selectedYear }
    }
    val filteredSubjects = subjects
        ?.filter { subject ->
            if (searchQuery.isBlank()) {
                true
            } else {
                val query = searchQuery.trim().lowercase()
                subject.name.lowercase().contains(query) ||
                    (subject.abbreviation?.lowercase()?.contains(query) == true)
            }
        }

    LaunchedEffect(viewModel) {
        viewModel.uiMessageRes.collect { messageRes ->
            Toast.makeText(context, context.getString(messageRes), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(selectedSemesterId) {
        viewModel.setSelectedSemester(selectedSemesterId)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ScrollAwareAddFab(
                listState = listState,
                onClick = { showAddDialog = true },
                contentDescription = stringResource(R.string.common_add),
                enabled = semesters.isNotEmpty()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.subject_greeting, userName),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.subject_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { yearMenuExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val yearLabel = selectedYear?.toString() ?: "-"
                                Text(
                                    text = "${stringResource(R.string.settings_semester_year_label)}: $yearLabel",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            DropdownMenu(
                                expanded = yearMenuExpanded,
                                onDismissRequest = { yearMenuExpanded = false },
                                modifier = Modifier.heightIn(max = 240.dp)
                            ) {
                                availableYears.forEach { year ->
                                    DropdownMenuItem(
                                        text = { Text(year.toString()) },
                                        onClick = {
                                            yearMenuExpanded = false
                                            val firstSemesterOfYear = semesters.firstOrNull { it.year == year }
                                            if (firstSemesterOfYear != null) {
                                                onSemesterSelected(firstSemesterOfYear.id)
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { semesterMenuExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = semestersOfSelectedYear.isNotEmpty()
                            ) {
                                val label = selectedSemester?.let { semester ->
                                    if (semester.season == SemesterSeason.AUTUMN) autumnLabel else springLabel
                                } ?: "-"
                                Text(
                                    text = "${stringResource(R.string.semester_label)}: $label",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            DropdownMenu(
                                expanded = semesterMenuExpanded,
                                onDismissRequest = { semesterMenuExpanded = false }
                            ) {
                                semestersOfSelectedYear.forEach { semester ->
                                    DropdownMenuItem(
                                        text = {
                                            val season = if (semester.season == SemesterSeason.AUTUMN) autumnLabel else springLabel
                                            Text(season)
                                        },
                                        onClick = {
                                            semesterMenuExpanded = false
                                            onSemesterSelected(semester.id)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        StatPill(
                            text = stringResource(
                                R.string.subject_count_chip,
                                subjects?.size ?: 0
                            )
                        )
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.subject_search_placeholder)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.common_cancel)
                            )
                        }
                    }
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                val currentSubjects = filteredSubjects
                if (semesters.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.subject_no_semesters),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else if (currentSubjects == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (currentSubjects.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.subject_empty), color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentSubjects) { subject ->
                            SubjectCard(
                                subject = subject,
                                onClick = { onSubjectClick(subject.id) },
                                onEditClick = {
                                    subjectToEdit = subject
                                    viewModel.loadSubjectSemesterIds(subject.id) { ids ->
                                        editSubjectSemesterIds = ids
                                    }
                                },
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
            semesters = semesters,
            isSemesterSelectionEnabled = true,
            initialSelectedSemesterIds = selectedSemesterId?.let { setOf(it) } ?: emptySet(),
            onDismiss = { showAddDialog = false },
            onConfirm = { name, abbreviation, semesterIds ->
                viewModel.addSubject(name, abbreviation, semesterIds) { result ->
                    if (result == EntityWriteResult.SUCCESS) {
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
            semesters = semesters,
            isSemesterSelectionEnabled = true,
            initialSelectedSemesterIds = editSubjectSemesterIds,
            onDismiss = { subjectToEdit = null },
            onConfirm = { newName, newAbbreviation, semesterIds ->
                viewModel.updateSubject(
                    subject = subject.copy(name = newName, abbreviation = newAbbreviation),
                    semesterIds = semesterIds
                ) { result ->
                    if (result == EntityWriteResult.SUCCESS) {
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
private fun StatPill(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f),
        shape = MaterialTheme.shapes.large
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
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
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal)
                )
                subject.abbreviation?.let {
                    if (it.isNotBlank()) {
                        Surface(
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.padding(start = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .width(48.dp)
                        .height(36.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f),
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.common_edit),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .width(48.dp)
                        .height(36.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f),
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.common_delete),
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectDialog(
    title: String,
    initialName: String = "",
    initialAbbreviation: String = "",
    semesters: List<Semester>,
    isSemesterSelectionEnabled: Boolean,
    initialSelectedSemesterIds: Set<Long>,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, List<Long>) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var abbreviation by remember { mutableStateOf(initialAbbreviation) }
    var selectedSemesterIds by remember(initialSelectedSemesterIds) { mutableStateOf(initialSelectedSemesterIds) }
    val autumnLabel = stringResource(R.string.settings_semester_autumn)
    val springLabel = stringResource(R.string.settings_semester_spring)

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
                if (isSemesterSelectionEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.subject_semesters_label),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        semesters.forEach { semester ->
                            val isChecked = semester.id in selectedSemesterIds
                            val seasonLabel = if (semester.season == SemesterSeason.AUTUMN) autumnLabel else springLabel
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        selectedSemesterIds = if (checked) {
                                            selectedSemesterIds + semester.id
                                        } else {
                                            selectedSemesterIds - semester.id
                                        }
                                    }
                                )
                                Text(
                                    text = stringResource(
                                        R.string.subject_semester_checkbox_item,
                                        seasonLabel,
                                        semester.year
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(name, abbreviation.ifBlank { null }, selectedSemesterIds.toList())
                },
                enabled = name.isNotBlank() && (!isSemesterSelectionEnabled || selectedSemesterIds.isNotEmpty())
            ) { Text(stringResource(R.string.common_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) }
        }
    )
}
