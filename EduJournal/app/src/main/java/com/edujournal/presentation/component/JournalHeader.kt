package com.edujournal.presentation.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.edujournal.domain.model.Lesson

@Composable
fun JournalHeader(
    lessons: List<Lesson>,
    scrollState: ScrollState
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Студент",
            modifier = Modifier
                .width(140.dp)
                .padding(8.dp),
            fontWeight = FontWeight.Bold
        )

        Row(modifier = Modifier.horizontalScroll(scrollState)) {
            lessons.forEach { lesson ->
                Text(
                    text = lesson.date.toString(),
                    modifier = Modifier
                        .width(60.dp)
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}