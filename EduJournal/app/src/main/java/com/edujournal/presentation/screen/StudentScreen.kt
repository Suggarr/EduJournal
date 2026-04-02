package com.edujournal.presentation.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.domain.model.Student
import com.edujournal.presentation.studentimport.StudentImportFileParser
import com.edujournal.presentation.studentimport.StudentImportInstructionDialog
import com.edujournal.presentation.studentimport.StudentImportParseResult
import com.edujournal.presentation.viewmodel.StudentViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(
    groupId: Long,
    onBackClick: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    LaunchedEffect(groupId) {
        viewModel.load(groupId)
    }

    val students by viewModel.students.collectAsState()
    val groupName by viewModel.groupName.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }
    var studentToEdit by remember { mutableStateOf<Student?>(null) }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        isImporting = true
        scope.launch {
            when (val result = StudentImportFileParser.parse(context, uri)) {
                is StudentImportParseResult.Success -> {
                    if (result.students.isEmpty()) {
                        isImporting = false
                        Toast.makeText(
                            context,
                            context.getString(R.string.student_import_empty),
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    }

                    viewModel.importStudents(
                        groupId = groupId,
                        importedStudents = result.students
                    ) { added, skipped ->
                        isImporting = false
                        Toast.makeText(
                            context,
                            context.getString(R.string.student_import_result, added, skipped),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                is StudentImportParseResult.Error -> {
                    isImporting = false
                    Toast.makeText(
                        context,
                        context.getString(R.string.student_import_parse_error, result.reason),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.student_group_title, groupName ?: groupId.toString()))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showImportDialog = true }) {
                        Text(stringResource(R.string.student_import_action))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.student_add_desc))
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isImporting) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (students.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.student_empty),
                            color = MaterialTheme.colorScheme.outline
                        )
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
                                onDeleteClick = { studentToDelete = student }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showImportDialog) {
        StudentImportInstructionDialog(
            onDismiss = { showImportDialog = false },
            onPickFile = {
                showImportDialog = false
                importLauncher.launch(StudentImportFileParser.supportedMimeTypes)
            }
        )
    }

    studentToDelete?.let { student ->
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text(stringResource(R.string.student_delete_title)) },
            text = {
                Text(
                    stringResource(R.string.student_delete_confirm, student.lastName, student.firstName)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStudent(student.id)
                        studentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.common_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToDelete = null }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }

    if (showAddDialog) {
        StudentDialog(
            title = stringResource(R.string.student_new),
            onDismiss = { showAddDialog = false },
            onConfirm = { firstName, lastName, middleName ->
                viewModel.addStudent(firstName, lastName, middleName, groupId)
                showAddDialog = false
            }
        )
    }

    studentToEdit?.let { student ->
        StudentDialog(
            title = stringResource(R.string.student_edit),
            initialFirstName = student.firstName,
            initialLastName = student.lastName,
            initialMiddleName = student.middleName,
            onDismiss = { studentToEdit = null },
            onConfirm = { firstName, lastName, middleName ->
                viewModel.updateStudent(
                    student.copy(
                        firstName = firstName,
                        lastName = lastName,
                        middleName = middleName
                    )
                )
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
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${student.lastName} ${student.firstName} ${student.middleName}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.common_edit),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.common_delete),
                    tint = MaterialTheme.colorScheme.error
                )
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
                    label = { Text(stringResource(R.string.student_last_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(stringResource(R.string.student_first_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = middleName,
                    onValueChange = { middleName = it },
                    label = { Text(stringResource(R.string.student_middle_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(firstName, lastName, middleName) },
                enabled = firstName.isNotBlank() && lastName.isNotBlank()
            ) {
                Text(stringResource(R.string.common_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    )
}
