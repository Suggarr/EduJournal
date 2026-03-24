package com.edujournal.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.domain.model.Group
import com.edujournal.presentation.component.GroupCard
import com.edujournal.presentation.component.GroupDialog
import com.edujournal.presentation.viewmodel.GroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    onGroupClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    viewModel: GroupViewModel = hiltViewModel()
) {

    val groups by viewModel.groups.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var groupToEdit by remember { mutableStateOf<Group?>(null) }
    var groupToDelete by remember { mutableStateOf<Group?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выберите группу") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            if (groups.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет групп")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(groups) { group ->
                        GroupCard(
                            group = group,
                            onClick = { onGroupClick(group.id) },
                            onEdit = { groupToEdit = group },
                            onDelete = { groupToDelete = group }
                        )
                    }
                }
            }
        }
    }

    // ➕ Добавление
    if (showAddDialog) {
        GroupDialog(
            title = "Новая группа",
            onDismiss = { showAddDialog = false },
            onConfirm = {
                viewModel.addGroup(it)
                showAddDialog = false
            }
        )
    }

    // ✏️ Редактирование
    groupToEdit?.let { group ->
        GroupDialog(
            title = "Редактировать группу",
            initialName = group.name,
            onDismiss = { groupToEdit = null },
            onConfirm = {
                viewModel.updateGroup(group.copy(name = it))
                groupToEdit = null
            }
        )
    }

    // ❗ Удаление (с подтверждением)
    groupToDelete?.let { group ->
        AlertDialog(
            onDismissRequest = { groupToDelete = null },
            title = { Text("Удалить группу?") },
            text = { Text("Это действие нельзя отменить") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteGroup(group.id)
                        groupToDelete = null
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { groupToDelete = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}