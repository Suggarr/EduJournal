package com.edujournal.presentation.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.edujournal.presentation.state.JournalCell
import com.edujournal.presentation.state.JournalRow

@Composable
fun JournalRowView(
    row: JournalRow,
    scrollState: ScrollState,
    onCellClick: (JournalCell) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Фиксированное имя студента
        Text(
            text = row.studentName,
            modifier = Modifier
                .width(140.dp)
                .padding(8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Прокручиваемые оценки
        Row(modifier = Modifier.horizontalScroll(scrollState)) {
            row.cells.forEach { cell ->
                JournalCellView(
                    cell = cell,
                    modifier = Modifier
                        .width(60.dp)
                        .clickable { onCellClick(cell) }
                )
            }
        }
    }
}