package com.edujournal.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.presentation.viewmodel.AnalyticsViewModel
import com.edujournal.presentation.viewmodel.StudentDisciplineAverage
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    semesterId: Long,
    groupId: Long,
    subjectId: Long,
    onBack: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val stateFlow = remember(semesterId, groupId, subjectId) {
        viewModel.observeState(
            groupId = groupId,
            subjectId = subjectId,
            semesterId = semesterId
        )
    }
    val state by stateFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.analytics_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        val ui = state
        if (ui == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = stringResource(
                        R.string.analytics_context,
                        ui.subjectName.ifBlank { "-" },
                        ui.groupName.ifBlank { "-" }
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            item {
                Text(
                    text = stringResource(R.string.analytics_ranking_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (ui.ranking.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.analytics_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                itemsIndexed(ui.ranking) { index, student ->
                    RankingCard(rank = index + 1, student = student)
                }
            }

            item {
                Text(
                    text = stringResource(R.string.analytics_debtors_title, ui.debtorThreshold),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (ui.debtors.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.analytics_debtors_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                itemsIndexed(ui.debtors) { _, student ->
                    DebtorRow(student = student)
                }
            }
        }
    }
}

@Composable
private fun RankingCard(rank: Int, student: StudentDisciplineAverage) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "$rank. ${student.studentName}",
                style = MaterialTheme.typography.titleSmall
            )
            val avgText = student.average?.let {
                String.format(Locale.US, "%.2f", it)
            } ?: "-"
            Text(
                text = stringResource(R.string.analytics_avg_line, avgText, student.gradedCount),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DebtorRow(student: StudentDisciplineAverage) {
    val avgText = student.average?.let {
        String.format(Locale.US, "%.2f", it)
    } ?: "-"
    Text(
        text = "${student.studentName} - $avgText",
        color = MaterialTheme.colorScheme.error
    )
}
