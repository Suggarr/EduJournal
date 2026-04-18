package com.edujournal.presentation.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.domain.model.GradeType
import com.edujournal.domain.model.SemesterSeason
import com.edujournal.presentation.component.JournalHeader
import com.edujournal.presentation.component.JournalRowView
import com.edujournal.presentation.journalexport.JournalExportFormat
import com.edujournal.presentation.journalexport.JournalExportWriter
import com.edujournal.presentation.state.JournalCell
import com.edujournal.presentation.state.JournalRow
import com.edujournal.presentation.viewmodel.JournalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    groupId: Long,
    subjectLessonTypeId: Long,
    semesterId: Long,
    onBack: () -> Unit,
    onTopicsClick: () -> Unit,
    viewModel: JournalViewModel = hiltViewModel()
) {
    val journalFlow = remember(groupId, subjectLessonTypeId, semesterId) {
        viewModel.observeJournal(groupId, subjectLessonTypeId, semesterId)
    }
    val state by journalFlow.collectAsState()
    val journalMetaFlow = remember(groupId, subjectLessonTypeId, semesterId) {
        viewModel.observeJournalMeta(groupId, subjectLessonTypeId, semesterId)
    }
    val journalMeta by journalMetaFlow.collectAsState()
    val autumnLabel = stringResource(R.string.settings_semester_autumn)
    val springLabel = stringResource(R.string.settings_semester_spring)
    val semesterFallback = stringResource(R.string.journal_semester_fallback, semesterId)
    val semesterLabel = remember(journalMeta) {
        val seasonLabel = when (journalMeta?.semesterSeason) {
            SemesterSeason.AUTUMN.name -> autumnLabel
            SemesterSeason.SPRING.name -> springLabel
            else -> null
        }
        if (seasonLabel != null && journalMeta?.semesterYear != null) {
            "$seasonLabel ${journalMeta?.semesterYear}"
        } else {
            semesterFallback
        }
    }

    val horizontalScrollState = rememberScrollState()
    var selectedCell by remember { mutableStateOf<Pair<JournalRow, JournalCell>?>(null) }
    var selectedExportFormat by remember { mutableStateOf<JournalExportFormat?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showActionsMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        val format = selectedExportFormat
        val currentState = state
        if (uri == null || format == null || currentState == null) return@rememberLauncherForActivityResult

        scope.launch(Dispatchers.IO) {
            val result = runCatching {
                JournalExportWriter.export(
                    resolver = context.contentResolver,
                    uri = uri,
                    format = format,
                    state = currentState,
                    meta = journalMeta,
                    semesterLabel = semesterLabel
                )
            }
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    Toast.makeText(context, context.getString(R.string.journal_export_success), Toast.LENGTH_SHORT).show()
                } else {
                    val message = result.exceptionOrNull()?.message ?: context.getString(R.string.journal_export_failed)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.journal_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showActionsMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.journal_actions_button)
                            )
                        }
                        DropdownMenu(
                            expanded = showActionsMenu,
                            onDismissRequest = { showActionsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.journal_export_button)) },
                                onClick = {
                                    showActionsMenu = false
                                    val currentState = state
                                    if (currentState == null || currentState.rows.isEmpty() || currentState.lessons.isEmpty()) {
                                        Toast.makeText(context, context.getString(R.string.journal_export_no_data), Toast.LENGTH_SHORT).show()
                                    } else {
                                        showExportDialog = true
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.journal_topics_button)) },
                                onClick = {
                                    showActionsMenu = false
                                    onTopicsClick()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val currentState = state
        if (currentState == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (currentState.rows.isEmpty() || currentState.lessons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.journal_empty))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                journalMeta?.let { meta ->
                    Text(
                        text = stringResource(
                            R.string.journal_context_line,
                            meta.subjectLabel.ifBlank { "-" },
                            meta.lessonTypeLabel.ifBlank { "-" },
                            meta.groupLabel.ifBlank { "-" }
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                JournalHeader(
                    lessons = currentState.lessons,
                    homeworkLessonIds = currentState.homeworkLessonIds,
                    scrollState = horizontalScrollState
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(currentState.rows) { rowIndex, row ->
                        JournalRowView(
                            row = row,
                            rowIndex = rowIndex,
                            scrollState = horizontalScrollState,
                            onCellClick = { cell ->
                                selectedCell = row to cell
                            }
                        )
                    }
                }
            }
        }
    }

    selectedCell?.let { (row, cell) ->
        GradeSelectionDialog(
            onDismiss = { selectedCell = null },
            onNumericGradeSelected = { grade ->
                viewModel.setNumericGrade(row.studentId, cell.lessonId, grade)
                selectedCell = null
            },
            onTypeSelected = { type ->
                viewModel.setGradeType(row.studentId, cell.lessonId, type)
                selectedCell = null
            },
            onClear = {
                viewModel.clearGrade(row.studentId, cell.lessonId)
                selectedCell = null
            }
        )
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text(stringResource(R.string.journal_export_title)) },
            text = { Text(stringResource(R.string.journal_export_description)) },
            confirmButton = {
                Row {
                    TextButton(onClick = {
                        val fileName = JournalExportWriter.buildDetailedFileName(
                            meta = journalMeta,
                            semesterLabel = semesterLabel,
                            format = JournalExportFormat.PDF
                        )
                        selectedExportFormat = JournalExportFormat.PDF
                        showExportDialog = false
                        exportLauncher.launch(fileName)
                    }) {
                        Text("PDF")
                    }
                    TextButton(onClick = {
                        val fileName = JournalExportWriter.buildDetailedFileName(
                            meta = journalMeta,
                            semesterLabel = semesterLabel,
                            format = JournalExportFormat.EXCEL
                        )
                        selectedExportFormat = JournalExportFormat.EXCEL
                        showExportDialog = false
                        exportLauncher.launch(fileName)
                    }) {
                        Text("Excel")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeSelectionDialog(
    onDismiss: () -> Unit,
    onNumericGradeSelected: (Int) -> Unit,
    onTypeSelected: (GradeType) -> Unit,
    onClear: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.journal_set_grade)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(stringResource(R.string.journal_grade_legend))
                Spacer(modifier = Modifier.height(8.dp))
                (1..10).toList().chunked(3).forEach { chunk ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        chunk.forEach { grade ->
                            OutlinedButton(
                                onClick = { onNumericGradeSelected(grade) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 6.dp, bottom = 6.dp)
                            ) {
                                Text(
                                    text = grade.toString(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Clip
                                )
                            }
                        }
                        repeat(3 - chunk.size) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 6.dp, bottom = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { onTypeSelected(GradeType.ABSENT) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 6.dp)
                    ) {
                        Text("\u041D")
                    }
                    Button(
                        onClick = { onTypeSelected(GradeType.SICK) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 6.dp)
                    ) {
                        Text("\u0417")
                    }
                    Button(
                        onClick = { onTypeSelected(GradeType.PASS) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("\u041E")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onClear, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.journal_clear_grade))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    )
}

