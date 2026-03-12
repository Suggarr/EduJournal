package com.edujournal.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import com.edujournal.domain.model.Lesson

@Composable
fun JournalHeader(
    lessons: List<Lesson>
) {

    Row {

        Text(
            text = "Студент",
            modifier = Modifier.width(140.dp)
        )

        lessons.forEach { lesson ->

            Text(
                text = lesson.date.toString(),
                modifier = Modifier.width(60.dp)
            )

        }

    }

}