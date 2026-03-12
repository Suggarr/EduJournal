package com.edujournal.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edujournal.presentation.state.JournalCell

@Composable
fun JournalCellView(
    cell: JournalCell
) {

    Box(
        modifier = Modifier
            .width(60.dp)
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = cell.value ?: "-"
        )

    }

}