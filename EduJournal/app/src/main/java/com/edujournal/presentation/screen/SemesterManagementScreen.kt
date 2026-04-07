package com.edujournal.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.edujournal.domain.model.Semester
import com.edujournal.domain.model.SemesterSeason
import com.edujournal.presentation.viewmodel.SemesterViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterManagementScreen(
    onBackClick: () -> Unit,
    semesterViewModel: SemesterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val semesters by semesterViewModel.semesters.collectAsState()
    val autumnLabel = stringResource(R.string.settings_semester_autumn)
    val springLabel = stringResource(R.string.settings_semester_spring)

    val semesterLastDeleteError = stringResource(R.string.settings_semester_last_delete_error)
    val semesterInvalidYearError = stringResource(R.string.settings_semester_invalid_year)
    val semesterDuplicateSeasonYearError = stringResource(R.string.settings_semester_duplicate_season_year)

    var showAddSemesterDialog by remember { mutableStateOf(false) }
    var semesterToEdit by remember { mutableStateOf<Semester?>(null) }
    var semesterToDelete by remember { mutableStateOf<Semester?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_semesters_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddSemesterDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.settings_semester_add)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(semesters, key = { it.id }) { semester ->
                    SemesterItemCard(
                        semester = semester,
                        autumnLabel = autumnLabel,
                        springLabel = springLabel,
                        onEditClick = { semesterToEdit = semester },
                        onDeleteClick = { semesterToDelete = semester }
                    )
                }
            }
        }
    }

    if (showAddSemesterDialog) {
        SemesterDialog(
            title = stringResource(R.string.settings_semester_add),
            onDismiss = { showAddSemesterDialog = false },
            onInvalidYear = {
                Toast.makeText(context, semesterInvalidYearError, Toast.LENGTH_SHORT).show()
            },
            onConfirm = { season, year ->
                val hasDuplicate = semesters.any { it.season == season && it.year == year }
                if (hasDuplicate) {
                    Toast.makeText(context, semesterDuplicateSeasonYearError, Toast.LENGTH_SHORT).show()
                    return@SemesterDialog
                }

                semesterViewModel.addSemester(season, year)
                showAddSemesterDialog = false
            }
        )
    }

    semesterToEdit?.let { semester ->
        SemesterDialog(
            title = stringResource(R.string.settings_semester_edit),
            initialSeason = semester.season,
            initialYear = semester.year.toString(),
            onDismiss = { semesterToEdit = null },
            onInvalidYear = {
                Toast.makeText(context, semesterInvalidYearError, Toast.LENGTH_SHORT).show()
            },
            onConfirm = { season, year ->
                val hasDuplicate = semesters.any {
                    it.id != semester.id && it.season == season && it.year == year
                }
                if (hasDuplicate) {
                    Toast.makeText(context, semesterDuplicateSeasonYearError, Toast.LENGTH_SHORT).show()
                    return@SemesterDialog
                }

                semesterViewModel.updateSemester(
                    semester.copy(season = season, year = year)
                )
                semesterToEdit = null
            }
        )
    }

    semesterToDelete?.let { semester ->
        AlertDialog(
            onDismissRequest = { semesterToDelete = null },
            title = { Text(stringResource(R.string.settings_semester_delete_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.settings_semester_delete_message,
                        semester.toDisplayName(autumnLabel, springLabel)
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (semesters.size <= 1) {
                            Toast.makeText(context, semesterLastDeleteError, Toast.LENGTH_SHORT).show()
                        } else {
                            semesterViewModel.deleteSemester(semester.id)
                        }
                        semesterToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.common_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { semesterToDelete = null }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}

@Composable
private fun SemesterItemCard(
    semester: Semester,
    autumnLabel: String,
    springLabel: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = semester.toDisplayName(autumnLabel, springLabel),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.common_edit)
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.common_delete)
                )
            }
        }
    }
}

@Composable
private fun SemesterDialog(
    title: String,
    initialSeason: SemesterSeason = SemesterSeason.AUTUMN,
    initialYear: String = "",
    onDismiss: () -> Unit,
    onInvalidYear: () -> Unit,
    onConfirm: (SemesterSeason, Int) -> Unit
) {
    val autumnLabel = stringResource(R.string.settings_semester_autumn)
    val springLabel = stringResource(R.string.settings_semester_spring)

    var selectedSeason by remember(initialSeason) { mutableStateOf(initialSeason) }
    var yearText by remember(initialYear) {
        mutableStateOf(initialYear.ifBlank { LocalDate.now().year.toString() })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = stringResource(R.string.settings_semester_season_label))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (selectedSeason == SemesterSeason.AUTUMN) {
                        Button(
                            onClick = { selectedSeason = SemesterSeason.AUTUMN },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = autumnLabel)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { selectedSeason = SemesterSeason.AUTUMN },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = autumnLabel)
                        }
                    }

                    if (selectedSeason == SemesterSeason.SPRING) {
                        Button(
                            onClick = { selectedSeason = SemesterSeason.SPRING },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = springLabel)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { selectedSeason = SemesterSeason.SPRING },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = springLabel)
                        }
                    }
                }

                OutlinedTextField(
                    value = yearText,
                    onValueChange = { input ->
                        yearText = input.filter { it.isDigit() }.take(4)
                    },
                    label = { Text(stringResource(R.string.settings_semester_year_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val year = yearText.toIntOrNull()
                    if (year == null || year < 2000 || year > 2100) {
                        onInvalidYear()
                        return@Button
                    }
                    onConfirm(selectedSeason, year)
                },
                enabled = yearText.length == 4
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

private fun Semester.toDisplayName(autumnLabel: String, springLabel: String): String {
    val seasonLabel = when (season) {
        SemesterSeason.AUTUMN -> autumnLabel
        SemesterSeason.SPRING -> springLabel
    }
    return "$seasonLabel $year"
}
