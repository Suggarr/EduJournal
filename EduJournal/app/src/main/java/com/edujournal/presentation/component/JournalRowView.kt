package com.edujournal.presentation.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.edujournal.presentation.state.JournalCell
import com.edujournal.presentation.state.JournalRow

@Composable
fun JournalRowView(
    row: JournalRow,
    rowIndex: Int,
    scrollState: ScrollState,
    onCellClick: (JournalCell) -> Unit,
    onCellLongClick: (JournalCell) -> Unit
) {
    val studentBg = if (rowIndex % 2 == 0) Color(0xFFF9F9F9) else Color.White

    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(40.dp)
                .background(studentBg)
                .border(1.dp, Color.Black)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = row.studentName,
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(modifier = Modifier.horizontalScroll(scrollState)) {
            row.cells.forEach { cell ->
                JournalCellView(
                    cell = cell,
                    modifier = Modifier
                        .width(96.dp)
                        .combinedClickable(
                            onClick = { onCellClick(cell) },
                            onLongClick = { onCellLongClick(cell) }
                        )
                )
            }
        }
    }
}
