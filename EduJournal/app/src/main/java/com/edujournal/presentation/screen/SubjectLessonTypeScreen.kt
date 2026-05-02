package com.edujournal.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.domain.model.SubjectLessonType
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.presentation.component.DeleteRectActionButton
import com.edujournal.presentation.component.EditRectActionButton
import com.edujournal.presentation.component.ScrollAwareAddFab
import com.edujournal.presentation.viewmodel.SubjectLessonTypeViewModel
import kotlinx.coroutines.flow.collect
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectLessonTypeScreen(
    subjectId: Long,
    onTypeClick: (Long) -> Unit,
    onTemplatesClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    viewModel: SubjectLessonTypeViewModel = hiltViewModel()
) {
    LaunchedEffect(subjectId) {
        viewModel.load(subjectId)
    }
    val types by viewModel.lessonTypes.collectAsState()
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var typeToEdit by remember { mutableStateOf<SubjectLessonType?>(null) }
    var typeToDelete by remember { mutableStateOf<SubjectLessonType?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel) {
        viewModel.uiMessageRes.collect { messageRes ->
            Toast.makeText(context, context.getString(messageRes), Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.subject_lesson_type_select),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ScrollAwareAddFab(
                listState = listState,
                onClick = { showAddDialog = true },
                contentDescription = stringResource(R.string.common_add)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            if (types.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.subject_lesson_type_empty), color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(types) { type ->
                        SubjectLessonTypeCard(
                            type = type,
                            onClick = { onTypeClick(type.id) },
                            onTemplatesClick = { onTemplatesClick(type.id) },
                            onEditClick = { typeToEdit = type },
                            onDeleteClick = { typeToDelete = type }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        SubjectLessonTypeDialog(
            title = stringResource(R.string.subject_lesson_type_new),
            onDismiss = { showAddDialog = false },
            onConfirm = { name, hours ->
                viewModel.addLessonType(name, hours) { result ->
                    if (result != EntityWriteResult.DUPLICATE) {
                        showAddDialog = false
                    }
                }
            }
        )
    }

    typeToEdit?.let { type ->
        SubjectLessonTypeDialog(
            title = stringResource(R.string.subject_lesson_type_edit),
            initialName = type.name,
            initialHours = type.hours,
            onDismiss = { typeToEdit = null },
            onConfirm = { newName, hours ->
                viewModel.updateLessonType(type.copy(name = newName, hours = hours)) { result ->
                    if (result != EntityWriteResult.DUPLICATE) {
                        typeToEdit = null
                    }
                }
            }
        )
    }

    typeToDelete?.let { type ->
        AlertDialog(
            onDismissRequest = { typeToDelete = null },
            title = { Text(stringResource(R.string.subject_lesson_type_delete_title)) },
            text = { Text(stringResource(R.string.subject_lesson_type_delete_message, type.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteLessonType(type.id)
                        typeToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.common_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { typeToDelete = null }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}

@Composable
fun SubjectLessonTypeCard(
    type: SubjectLessonType,
    onClick: () -> Unit,
    onTemplatesClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = type.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal)
                )
                type.hours?.let { hours ->
                    Text(
                        text = stringResource(
                            R.string.subject_lesson_type_hours_value,
                            BigDecimal.valueOf(hours).stripTrailingZeros().toPlainString().replace('.', ',')
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onTemplatesClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = stringResource(R.string.topic_template_title),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            EditRectActionButton(onClick = onEditClick)
            Spacer(modifier = Modifier.width(12.dp))
            DeleteRectActionButton(onClick = onDeleteClick)
        }
    }
}

@Composable
fun SubjectLessonTypeDialog(
    title: String,
    initialName: String = "",
    initialHours: Double? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, Double?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var hoursInput by remember {
        mutableStateOf(
            initialHours?.let { BigDecimal.valueOf(it).stripTrailingZeros().toPlainString().replace('.', ',') }
                .orEmpty()
        )
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.subject_lesson_type_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = hoursInput,
                    onValueChange = { value ->
                        val normalized = value.replace('.', ',')
                        val filtered = buildString {
                            var hasSeparator = false
                            for (ch in normalized) {
                                when {
                                    ch.isDigit() -> append(ch)
                                    ch == ',' && !hasSeparator -> {
                                        append(ch)
                                        hasSeparator = true
                                    }
                                }
                            }
                        }
                        hoursInput = filtered
                    },
                    label = { Text(stringResource(R.string.subject_lesson_type_hours_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hours = hoursInput.trim().replace(',', '.').toDoubleOrNull()
                    onConfirm(name, hours)
                },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.common_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) }
        }
    )
}


