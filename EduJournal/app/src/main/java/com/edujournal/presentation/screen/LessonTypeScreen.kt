package com.edujournal.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.domain.model.LessonType
import com.edujournal.presentation.viewmodel.LessonTypeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonTypeScreen(
    subjectId: Long,
    onTypeClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    viewModel: LessonTypeViewModel = hiltViewModel()
) {
    val types by viewModel.lessonTypes.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var typeToEdit by remember { mutableStateOf<LessonType?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выберите тип занятия") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            if (types.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Типы занятий не найдены", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(types) { type ->
                        LessonTypeCard(
                            type = type,
                            onClick = { onTypeClick(type.id) },
                            onEditClick = { typeToEdit = type },
                            onDeleteClick = { viewModel.deleteLessonType(type.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        LessonTypeDialog(
            title = "Новый тип занятия",
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                viewModel.addLessonType(name)
                showAddDialog = false
            }
        )
    }

    typeToEdit?.let { type ->
        LessonTypeDialog(
            title = "Редактировать",
            initialName = type.name,
            onDismiss = { typeToEdit = null },
            onConfirm = { newName ->
                viewModel.updateLessonType(type.copy(name = newName))
                typeToEdit = null
            }
        )
    }
}

@Composable
fun LessonTypeCard(
    type: LessonType,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = type.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Изменить", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun LessonTypeDialog(
    title: String,
    initialName: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название (например, Лекция)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(name) }, enabled = name.isNotBlank()) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}