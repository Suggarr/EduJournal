package com.edujournal.presentation.screen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.R
import com.edujournal.presentation.viewmodel.AnalyticsViewModel
import com.edujournal.presentation.viewmodel.GradeDistributionItem
import com.edujournal.presentation.viewmodel.GradeOverTimePoint
import com.edujournal.presentation.viewmodel.StudentDisciplineAverage
import com.edujournal.presentation.viewmodel.TypeAnalyticsSummary
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorPosition
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
                AnalyticsSummaryCard(
                    groupAverage = ui.groupAverage,
                    studentsCount = ui.studentsCount,
                    lessonsCount = ui.lessonsCount,
                    plannedHours = ui.plannedHours,
                    attendancePresentCount = ui.attendancePresentCount,
                    attendanceMarkedCount = ui.attendanceMarkedCount,
                    attendanceAbsentCount = ui.attendanceAbsentCount,
                    attendanceSickCount = ui.attendanceSickCount,
                    attendancePercent = ui.attendancePercent
                )
            }

            item {
                Text(
                    text = stringResource(R.string.analytics_over_time_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                OverTimeChartCard(points = ui.overTime)
            }

            item {
                Text(
                    text = stringResource(R.string.analytics_distribution_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                GradeDistributionCard(items = ui.gradeDistribution)
            }

            item {
                Text(
                    text = stringResource(R.string.analytics_type_stats_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (ui.typeSummaries.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.analytics_type_stats_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                itemsIndexed(ui.typeSummaries) { _, summary ->
                    TypeSummaryCard(summary = summary)
                }
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

@Composable
private fun AnalyticsSummaryCard(
    groupAverage: Double?,
    studentsCount: Int,
    lessonsCount: Int,
    plannedHours: Double?,
    attendancePresentCount: Int,
    attendanceMarkedCount: Int,
    attendanceAbsentCount: Int,
    attendanceSickCount: Int,
    attendancePercent: Double?
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.analytics_summary_title),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(
                    R.string.analytics_summary_group_avg,
                    groupAverage?.let { String.format(Locale.US, "%.2f", it) } ?: "-"
                )
            )
            Text(
                text = stringResource(R.string.analytics_summary_students, studentsCount)
            )
            Text(
                text = stringResource(R.string.analytics_summary_lessons, lessonsCount)
            )
            Text(
                text = stringResource(
                    R.string.analytics_summary_hours,
                    plannedHours?.let(::formatHours) ?: "-"
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(
                    R.string.analytics_summary_attendance,
                    attendancePresentCount,
                    attendanceMarkedCount,
                    attendancePercent?.let { String.format(Locale.US, "%.1f", it) } ?: "-"
                )
            )
            Text(
                text = stringResource(
                    R.string.analytics_summary_absent_sick,
                    attendanceAbsentCount,
                    attendanceSickCount
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TypeSummaryCard(summary: TypeAnalyticsSummary) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = summary.typeName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = stringResource(
                        R.string.analytics_type_hours,
                        summary.plannedHours?.let(::formatHours) ?: "-"
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = stringResource(R.string.analytics_type_lessons, summary.lessonsCount),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun OverTimeChartCard(points: List<GradeOverTimePoint>) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(modifier = Modifier.fillMaxWidth()) {
        val parsed = points.mapNotNull { point ->
            runCatching { LocalDate.parse(point.label) to point.average }.getOrNull()
        }
        if (parsed.isEmpty()) {
            Text(
                text = stringResource(R.string.analytics_over_time_empty),
                modifier = Modifier.padding(12.dp),
                color = secondaryTextColor
            )
        } else {
            val formatter = DateTimeFormatter.ofPattern("dd.MM")
            val labels = parsed.map { (date, _) -> date.format(formatter) }
            val values = parsed.map { (_, avg) -> avg }
            val lineLabel = stringResource(R.string.analytics_over_time_title)
            val chartTextStyle = MaterialTheme.typography.labelSmall.copy(color = secondaryTextColor)
            val xScrollState = rememberScrollState()
            val chartWidth = (labels.size * 56).coerceAtLeast(320).dp
            val didInitialAutoScroll = rememberSaveable { mutableStateOf(false) }
            LaunchedEffect(labels, didInitialAutoScroll.value) {
                if (!didInitialAutoScroll.value && labels.isNotEmpty()) {
                    xScrollState.scrollTo(xScrollState.maxValue)
                    didInitialAutoScroll.value = true
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    FixedYAxis(
                        min = 1,
                        max = 10,
                        steps = 10,
                        textStyle = chartTextStyle
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(xScrollState)
                    ) {
                        LineChart(
                            modifier = Modifier
                                .width(chartWidth)
                                .height(240.dp),
                            data = remember(values, labels) {
                                listOf(
                                    Line(
                                        label = lineLabel,
                                        values = values,
                                        color = SolidColor(primaryColor),
                                        firstGradientFillColor = primaryColor.copy(alpha = 0.20f),
                                        secondGradientFillColor = Color.Transparent,
                                        drawStyle = DrawStyle.Stroke(width = 2.dp),
                                        curvedEdges = true
                                    )
                                )
                            },
                            animationMode = AnimationMode.Together { index -> index * 100L },
                            minValue = 1.0,
                            maxValue = 10.0,
                            indicatorProperties = HorizontalIndicatorProperties(
                                enabled = false
                            ),
                            labelProperties = LabelProperties(
                                enabled = true,
                                textStyle = chartTextStyle,
                                labels = labels
                            ),
                            labelHelperProperties = LabelHelperProperties(enabled = false)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    Text(
                        text = stringResource(R.string.analytics_over_time_y_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = secondaryTextColor
                    )
                }
            }
        }
    }
}

@Composable
private fun GradeDistributionCard(items: List<GradeDistributionItem>) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(modifier = Modifier.fillMaxWidth()) {
        val maxCount = items.maxOfOrNull { it.count } ?: 0
        if (maxCount == 0) {
            Text(
                text = stringResource(R.string.analytics_distribution_empty),
                modifier = Modifier.padding(12.dp),
                color = secondaryTextColor
            )
        } else {
            val chartData = remember(items, primaryColor) {
                items.map { item ->
                    Bars(
                        label = item.grade.toString(),
                        values = listOf(
                            Bars.Data(
                                label = item.grade.toString(),
                                value = item.count.toDouble(),
                                color = SolidColor(primaryColor)
                            )
                        )
                    )
                }
            }

            val chartTextStyle = MaterialTheme.typography.labelSmall.copy(color = secondaryTextColor)

            Column(modifier = Modifier.padding(12.dp)) {
                RowChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    data = chartData,
                    maxValue = maxCount.toDouble(),
                    minValue = 0.0,
                    animationMode = AnimationMode.Together { it * 60L },
                    barProperties = BarProperties(
                        thickness = 16.dp,
                        spacing = 8.dp,
                        style = DrawStyle.Fill
                    ),
                    indicatorProperties = VerticalIndicatorProperties(
                        enabled = true,
                        position = IndicatorPosition.Vertical.Bottom,
                        textStyle = chartTextStyle,
                        contentBuilder = { value -> value.toInt().toString() }
                    ),
                    labelProperties = LabelProperties(
                        enabled = true,
                        textStyle = chartTextStyle,
                        labels = items.map { it.grade.toString() }
                    ),
                    labelHelperProperties = LabelHelperProperties(enabled = false)
                )

                Text(
                    text = stringResource(R.string.analytics_distribution_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryTextColor,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun FixedYAxis(
    min: Int,
    max: Int,
    steps: Int,
    textStyle: TextStyle
) {
    val values = remember(min, max, steps) {
        val safeSteps = steps.coerceAtLeast(1)
        val stepValue = (max - min).toFloat() / safeSteps.toFloat()
        (0..safeSteps).map { index ->
            (max - (index * stepValue)).toInt()
        }
    }
    Column(
        modifier = Modifier
            .width(28.dp)
            .fillMaxSize()
            .padding(end = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.End
    ) {
        values.forEach { value ->
            Text(
                text = value.toString(),
                style = textStyle
            )
        }
    }
}

private fun formatHours(value: Double): String {
    return BigDecimal.valueOf(value)
        .stripTrailingZeros()
        .toPlainString()
        .replace('.', ',')
}
