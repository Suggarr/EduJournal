package com.edujournal.presentation.studentimport

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.edujournal.R

@Composable
fun StudentImportInstructionDialog(
    onDismiss: () -> Unit,
    onPickFile: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.student_import_title)) },
        text = { Text(stringResource(R.string.student_import_instructions)) },
        confirmButton = {
            Button(onClick = onPickFile) {
                Text(stringResource(R.string.student_import_choose_file))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    )
}
