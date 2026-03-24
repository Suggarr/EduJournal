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
import com.edujournal.domain.model.Student
import com.edujournal.presentation.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(
    groupId: Long,
    onBackClick: () -> Unit,
    onOpenJournal: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    // Вызываем загрузку при первом появлении экрана
    LaunchedEffect(groupId) {
        viewModel.load(groupId)
    }

    val students by viewModel.students.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var studentToEdit by remember { mutableStateOf<Student?>(null) }
    var studentToDelete by remember { mutableStateOf<Student?>(null) } // Состояние для подтверждения удаления

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Студенты группы") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить студента")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            if (students.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("В группе пока нет студентов", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(students) { student ->
                        StudentCard(
                            student = student,
                            onEditClick = { studentToEdit = student },
                            onDeleteClick = { studentToDelete = student } // Запоминаем студента для удаления
                        )
                    }
                }
            }
        }
    }

    // Диалог подтверждения удаления
    studentToDelete?.let { student ->
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Удалить студента?") },
            text = {
                Text("Вы действительно хотите удалить студента ${student.lastName} ${student.firstName}? Это действие нельзя отменить.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStudent(student.id)
                        studentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToDelete = null }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showAddDialog) {
        StudentDialog(
            title = "Новый студент",
            onDismiss = { showAddDialog = false },
            onConfirm = { firstName, lastName, middleName ->
                viewModel.addStudent(firstName, lastName, middleName, groupId)
                showAddDialog = false
            }
        )
    }

    studentToEdit?.let { student ->
        StudentDialog(
            title = "Редактировать",
            initialFirstName = student.firstName,
            initialLastName = student.lastName,
            initialMiddleName = student.middleName,
            onDismiss = { studentToEdit = null },
            onConfirm = { firstName, lastName, middleName ->
                viewModel.updateStudent(student.copy(
                    firstName = firstName,
                    lastName = lastName,
                    middleName = middleName
                ))
                studentToEdit = null
            }
        )
    }
}

@Composable
fun StudentCard(
    student: Student,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${student.lastName} ${student.firstName} ${student.middleName}",
                style = MaterialTheme.typography.titleMedium,
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
fun StudentDialog(
    title: String,
    initialFirstName: String = "",
    initialLastName: String = "",
    initialMiddleName: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var firstName by remember { mutableStateOf(initialFirstName) }
    var lastName by remember { mutableStateOf(initialLastName) }
    var middleName by remember { mutableStateOf(initialMiddleName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Фамилия") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Имя") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = middleName,
                    onValueChange = { middleName = it },
                    label = { Text("Отчество") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(firstName, lastName, middleName) },
                enabled = firstName.isNotBlank() && lastName.isNotBlank()
            ) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}