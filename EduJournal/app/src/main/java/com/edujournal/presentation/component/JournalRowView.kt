package com.edujournal.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import com.edujournal.presentation.state.JournalRow

@Composable
fun JournalRowView(
    row: JournalRow
) {

    Row {

        Text(
            text = row.studentName,
            modifier = Modifier.width(140.dp)
        )

        row.cells.forEach { cell ->

            JournalCellView(cell)

        }

    }

}