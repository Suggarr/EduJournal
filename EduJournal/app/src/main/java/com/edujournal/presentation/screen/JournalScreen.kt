package com.edujournal.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.presentation.component.JournalHeader
import com.edujournal.presentation.component.JournalRowView
import com.edujournal.presentation.state.JournalCell
import com.edujournal.presentation.state.JournalRow
import com.edujournal.presentation.viewmodel.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    groupId: Long,
    onBack: () -> Unit,
    viewModel: JournalViewModel = hiltViewModel()
) {
    val state by viewModel.observeJournal(groupId).collectAsState()
    val horizontalScrollState = rememberScrollState()

    var selectedCell by remember { mutableStateOf<Pair<JournalRow, JournalCell>?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.journal_title, groupId.toString())) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.rows.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.journal_empty, groupId.toString()))
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
                    items(state.rows) { row ->
                        JournalRowView(
                            row = row,
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
            onGradeSelected = { gradeValue ->
                viewModel.setGrade(row.studentId, cell.lessonId, gradeValue)
                selectedCell = null
            }
        )
    }
}

@Composable
fun GradeSelectionDialog(
    onDismiss: () -> Unit,
    onGradeSelected: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.journal_set_grade)) },
        text = {
            Column {
                listOf(2, 3, 4, 5).forEach { grade ->
                    Button(
                        onClick = { onGradeSelected(grade) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(grade.toString())
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    )
}
