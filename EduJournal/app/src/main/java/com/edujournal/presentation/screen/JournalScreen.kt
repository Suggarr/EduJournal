package com.edujournal.presentation.screen

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.edujournal.domain.model.GradeType
import com.edujournal.presentation.component.JournalHeader
import com.edujournal.presentation.component.JournalRowView
import com.edujournal.presentation.state.JournalCell
import com.edujournal.presentation.state.JournalRow
import com.edujournal.presentation.viewmodel.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    groupId: Long,
    subjectId: Long,
    lessonTypeId: Long,
    onBack: () -> Unit,
    onTopicsClick: () -> Unit,
    viewModel: JournalViewModel = hiltViewModel()
) {
    val state by viewModel
        .observeJournal(groupId, subjectId, lessonTypeId)
        .collectAsState()

    val horizontalScrollState = rememberScrollState()
    var selectedCell by remember { mutableStateOf<Pair<JournalRow, JournalCell>?>(null) }

    Scaffold(
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
                    TextButton(onClick = onTopicsClick) {
                        Text(stringResource(R.string.journal_topics_button))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.rows.isEmpty() || state.lessons.isEmpty()) {
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
                JournalHeader(
                    lessons = state.lessons,
                    scrollState = horizontalScrollState
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(state.rows) { rowIndex, row ->
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
