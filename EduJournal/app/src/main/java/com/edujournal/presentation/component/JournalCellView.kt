package com.edujournal.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edujournal.presentation.state.JournalCell
import com.edujournal.presentation.state.JournalCellVisualStyles

@Composable
fun JournalCellView(
    cell: JournalCell,
    modifier: Modifier = Modifier
) {
    val visualStyle = JournalCellVisualStyles.forValue(cell.value)
    val bgColor = Color(visualStyle.backgroundArgb)
    val textColor = Color(visualStyle.textArgb)

    Box(
        modifier = modifier
            .width(96.dp)
            .height(40.dp)
            .background(bgColor)
            .border(1.dp, Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = cell.value ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )

        if (!cell.comment.isNullOrBlank()) {
            Text(
                text = "•",
                color = Color(0xFF1565C0),
                fontSize = 30.sp,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}
