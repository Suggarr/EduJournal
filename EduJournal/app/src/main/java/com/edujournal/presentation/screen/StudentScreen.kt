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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.edujournal.domain.model.Student
import com.edujournal.domain.usecase.common.EntityWriteResult
import com.edujournal.presentation.component.ScrollAwareAddFab
import com.edujournal.presentation.component.EditRectActionButton
import com.edujournal.presentation.component.DeleteRectActionButton
import com.edujournal.presentation.studentimport.StudentImportInstructionDialog
import com.edujournal.presentation.viewmodel.StudentImportEvent
import com.edujournal.presentation.viewmodel.StudentViewModel
import kotlinx.coroutines.flow.collect

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
    val isImporting by viewModel.isImporting.collectAsState()

    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var studentToEdit by remember { mutableStateOf<Student?>(null) }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel) {
        viewModel.uiMessageRes.collect { messageRes ->
            Toast.makeText(context, context.getString(messageRes), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.importEvents.collect { event ->
            when (event) {
                StudentImportEvent.Empty -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.student_import_empty),
                        Toast.LENGTH_LONG
                    ).show()
                }

                is StudentImportEvent.ParseError -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.student_import_parse_error, event.reason),
                        Toast.LENGTH_LONG
                    ).show()
                }

                is StudentImportEvent.Result -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.student_import_result, event.added, event.skipped),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        viewModel.importStudentsFromFile(
            groupId = groupId,
            uri = uri
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.student_group_title, groupName ?: groupId.toString()),
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
                },
                actions = {
                    TextButton(onClick = { showImportDialog = true }) {
                        Text(stringResource(R.string.student_import_action))
                    }
                }
            )
        },
        floatingActionButton = {
            ScrollAwareAddFab(
                listState = listState,
                onClick = { showAddDialog = true },
                contentDescription = stringResource(R.string.student_add_desc)
            )
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
                        state = listState,
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
                importLauncher.launch(viewModel.supportedImportMimeTypes)
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
                viewModel.addStudent(firstName, lastName, middleName, groupId) { result ->
                    if (result != EntityWriteResult.DUPLICATE) {
                        showAddDialog = false
                    }
                }
            }
        )
    }

    studentToEdit?.let { student ->
        StudentDialog(
            title = stringResource(R.string.student_edit),
            initialFirstName = student.firstName,
            initialLastName = student.lastName,
            initialMiddleName = student.middleName.orEmpty(),
            onDismiss = { studentToEdit = null },
            onConfirm = { firstName, lastName, middleName ->
                viewModel.updateStudent(
                    student.copy(
                        firstName = firstName,
                        lastName = lastName,
                        middleName = middleName
                    )
                ) { result ->
                    if (result != EntityWriteResult.DUPLICATE) {
                        studentToEdit = null
                    }
                }
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = listOf(student.lastName, student.firstName, student.middleName)
                    .filter { !it.isNullOrBlank() }
                    .joinToString(" "),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            EditRectActionButton(onClick = onEditClick)
            Spacer(modifier = Modifier.width(12.dp))
            DeleteRectActionButton(onClick = onDeleteClick)
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
    onConfirm: (String, String, String?) -> Unit
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
                onClick = { onConfirm(firstName, lastName, middleName.ifBlank { null }) },
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
