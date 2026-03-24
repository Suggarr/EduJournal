package com.edujournal.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.presentation.component.JournalHeader
import com.edujournal.presentation.component.JournalRowView
import com.edujournal.presentation.state.JournalCell
import com.edujournal.presentation.state.JournalRow
import com.edujournal.presentation.viewmodel.JournalViewModel

@Composable
fun JournalScreen(
    groupId: Long,
    viewModel: JournalViewModel = hiltViewModel()
) {
    val state by viewModel.observeJournal(groupId).collectAsState()
    val horizontalScrollState = rememberScrollState()

    var selectedCell by remember { mutableStateOf<Pair<JournalRow, JournalCell>?>(null) }
    if (state.rows.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Данных нет. Проверьте студентов и занятия для группы $groupId")
        }
    } else {


        Column(modifier = Modifier.fillMaxSize()) {
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
                        modifier = Modifier.fillMaxSize()
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