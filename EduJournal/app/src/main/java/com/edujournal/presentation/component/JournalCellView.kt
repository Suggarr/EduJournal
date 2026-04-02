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
import com.edujournal.presentation.state.JournalCell

@Composable
fun JournalCellView(
    cell: JournalCell,
    modifier: Modifier = Modifier
) {
    val bgColor = when (cell.value) {
        "1", "2", "3" -> Color(0xFFFFDAD6)
        "4", "5", "6" -> Color(0xFFFFECB3)
        "7", "8", "9", "10" -> Color(0xFFC8E6C9)
        "\u041D" -> Color(0xFFFFCC80)
        "\u0417" -> Color(0xFF90CAF9)
        "\u041E" -> Color(0xFFB39DDB)
        else -> Color.White
    }

    val textColor = when (cell.value) {
        "1", "2", "3" -> Color(0xFF7F1D1D)
        "4", "5", "6" -> Color(0xFF8A4B00)
        "7", "8", "9", "10" -> Color(0xFF1B5E20)
        "\u041D" -> Color(0xFF6D3500)
        "\u0417" -> Color(0xFF0D47A1)
        "\u041E" -> Color(0xFF311B92)
        else -> Color.Black
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
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}
