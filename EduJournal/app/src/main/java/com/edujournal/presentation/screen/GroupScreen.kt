package com.edujournal.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.domain.model.Group
import com.edujournal.domain.usecase.EntityWriteResult
import com.edujournal.presentation.component.GroupCard
import com.edujournal.presentation.component.GroupDialog
import com.edujournal.presentation.viewmodel.GroupViewModel
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    onGroupClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    showBackButton: Boolean = true,
    viewModel: GroupViewModel = hiltViewModel()
) {
    val groups by viewModel.groups.collectAsState()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var groupToEdit by remember { mutableStateOf<Group?>(null) }
    var groupToDelete by remember { mutableStateOf<Group?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.uiMessageRes.collect { messageRes ->
            Toast.makeText(context, context.getString(messageRes), Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.group_select)) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.common_back)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.common_add))
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (groups.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.group_empty))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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

    if (showAddDialog) {
        GroupDialog(
            title = stringResource(R.string.group_new),
            onDismiss = { showAddDialog = false },
            onConfirm = {
                viewModel.addGroup(it) { result ->
                    if (result != EntityWriteResult.DUPLICATE) {
                        showAddDialog = false
                    }
                }
            }
        )
    }

    groupToEdit?.let { group ->
        GroupDialog(
            title = stringResource(R.string.group_edit),
            initialName = group.name,
            onDismiss = { groupToEdit = null },
            onConfirm = {
                viewModel.updateGroup(group.copy(name = it)) { result ->
                    if (result != EntityWriteResult.DUPLICATE) {
                        groupToEdit = null
                    }
                }
            }
        )
    }

    groupToDelete?.let { group ->
        AlertDialog(
            onDismissRequest = { groupToDelete = null },
            title = { Text(stringResource(R.string.group_delete_title)) },
            text = { Text(stringResource(R.string.group_delete_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteGroup(group.id)
                        groupToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.common_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { groupToDelete = null }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}
