package com.edujournal.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.edujournal.presentation.state.JournalCell

@Composable
fun JournalCellView(
    cell: JournalCell,
    modifier: Modifier = Modifier
) {
    val bgColor = when (cell.value) {
        "1", "2", "3" -> Color(0xFFFFEBEE)
        "4", "5", "6" -> Color(0xFFFFF8E1)
        "7", "8", "9", "10" -> Color(0xFFE8F5E9)
        "\u041D" -> Color(0xFFFFF3E0) // Н
        "\u0417" -> Color(0xFFE3F2FD) // З
        "\u041E" -> Color(0xFFE8EAF6) // О
        else -> Color.White
    }

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
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
