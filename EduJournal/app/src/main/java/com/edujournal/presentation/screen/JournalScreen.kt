package com.edujournal.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
                title = { Text("Журнал группы: $groupId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
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
                Text("Данных нет. Проверьте студентов и занятия для группы $groupId")
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

    // Диалог выбора оценки
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
        title = { Text("Поставить оценку") },
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
                Text("Отмена")
            }
        }
    )
}