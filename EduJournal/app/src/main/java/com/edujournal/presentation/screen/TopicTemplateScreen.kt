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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.domain.model.TopicTemplate
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.presentation.component.DeleteRectActionButton
import com.edujournal.presentation.component.EditRectActionButton
import com.edujournal.presentation.component.ScrollAwareAddFab
import com.edujournal.presentation.viewmodel.TopicTemplateViewModel
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicTemplateScreen(
    semesterId: Long,
    subjectLessonTypeId: Long,
    onBackClick: () -> Unit,
    viewModel: TopicTemplateViewModel = hiltViewModel()
) {
    LaunchedEffect(semesterId, subjectLessonTypeId) {
        viewModel.load(semesterId, subjectLessonTypeId)
    }

    val templates by viewModel.templates.collectAsState()
    val context = LocalContext.current
    var showBatchDialog by remember { mutableStateOf(false) }
    var batchInput by remember { mutableStateOf("") }
    var templateToEdit by remember { mutableStateOf<TopicTemplate?>(null) }
    var templateToDelete by remember { mutableStateOf<TopicTemplate?>(null) }
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
                        text = stringResource(R.string.topic_template_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back))
                    }
                }
            )
        },
        floatingActionButton = {
            ScrollAwareAddFab(
                listState = listState,
                onClick = { showBatchDialog = true },
                contentDescription = stringResource(R.string.topic_template_batch_add)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            if (templates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.topic_template_empty), color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(templates) { template ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${template.orderInType}. ${template.title}",
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                EditRectActionButton(onClick = { templateToEdit = template })
                                Spacer(modifier = Modifier.width(12.dp))
                                DeleteRectActionButton(onClick = { templateToDelete = template })
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBatchDialog) {
        AlertDialog(
            onDismissRequest = { showBatchDialog = false },
            title = { Text(stringResource(R.string.topic_template_batch_dialog_title)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.topic_template_batch_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = batchInput,
                        onValueChange = { batchInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 140.dp, max = 260.dp),
                        minLines = 7,
                        label = { Text(stringResource(R.string.topic_template_batch_input_label)) },
                        placeholder = { Text(stringResource(R.string.topic_template_batch_example)) }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addTemplatesBatch(batchInput) { added ->
                        Toast.makeText(
                            context,
                            context.getString(R.string.topic_template_batch_result, added),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    batchInput = ""
                    showBatchDialog = false
                }, enabled = batchInput.isNotBlank()) {
                    Text(stringResource(R.string.topic_template_batch_apply))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBatchDialog = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }

    templateToEdit?.let { template ->
        TopicTemplateDialog(
            title = stringResource(R.string.topic_template_edit),
            initialTitle = template.title,
            onDismiss = { templateToEdit = null },
            onConfirm = { title ->
                viewModel.updateTemplate(template, title) { result ->
                    if (result == EntityWriteResult.SUCCESS) templateToEdit = null
                }
            }
        )
    }

    templateToDelete?.let { template ->
        AlertDialog(
            onDismissRequest = { templateToDelete = null },
            title = { Text(stringResource(R.string.topic_template_delete_title)) },
            text = { Text(stringResource(R.string.topic_template_delete_message, template.title)) },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteTemplate(template.id)
                    templateToDelete = null
                }) { Text(stringResource(R.string.common_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { templateToDelete = null }) { Text(stringResource(R.string.common_cancel)) }
            }
        )
    }
}

@Composable
private fun TopicTemplateDialog(
    title: String,
    initialTitle: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var value by remember { mutableStateOf(initialTitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.topic_template_name_label)) },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(value.trim()) }, enabled = value.isNotBlank()) {
                Text(stringResource(R.string.common_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) }
        }
    )
}


