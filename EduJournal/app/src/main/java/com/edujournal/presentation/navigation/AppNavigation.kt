package com.edujournal.presentation.navigation

import android.widget.Toast
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.layout.WindowInsets
import com.edujournal.R
import com.edujournal.presentation.screen.GroupScreen
import com.edujournal.presentation.screen.AnalyticsScreen
import com.edujournal.presentation.screen.HomeworkScreen
import com.edujournal.presentation.screen.JournalScreen
import com.edujournal.presentation.screen.LessonTopicsScreen
import com.edujournal.presentation.screen.SubjectLessonTypeScreen
import com.edujournal.presentation.screen.SemesterManagementScreen
import com.edujournal.presentation.screen.SettingsScreen
import com.edujournal.presentation.screen.StudentScreen
import com.edujournal.presentation.screen.SubjectScreen
import com.edujournal.presentation.screen.WelcomeScreen
import com.edujournal.presentation.viewmodel.MainViewModel
import com.edujournal.presentation.viewmodel.SettingsEvent

private data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: @Composable () -> Unit
)

@Composable
fun AppNavigation(
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val fixedBottomLabelSizeSp = (12f / density.fontScale).sp
    val navController = rememberNavController()
    val userName by viewModel.userName
    val biometricEnabled by viewModel.biometricEnabled
    val semesters by viewModel.semesters.collectAsState()
    val selectedSemesterId by viewModel.selectedSemesterId
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val semesterRequiredMessage = stringResource(R.string.semester_required_for_navigation)

    LaunchedEffect(viewModel, context) {
        viewModel.settingsEvents.collect { event ->
            when (event) {
                is SettingsEvent.Message -> {
                    Toast.makeText(context, context.getString(event.resId), Toast.LENGTH_SHORT).show()
                }
                is SettingsEvent.MessageText -> {
                    Toast.makeText(context, event.text, Toast.LENGTH_LONG).show()
                }
                is SettingsEvent.ShareDatabase -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/octet-stream"
                        putExtra(Intent.EXTRA_STREAM, event.uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            context.getString(R.string.settings_share_db)
                        )
                    )
                }
                SettingsEvent.RestartRequired -> restartApplication(context)
            }
        }
    }

    val startDestination = if (userName == null) Routes.WELCOME else Routes.SUBJECTS

    val bottomItems = listOf(
        BottomNavItem(
            route = Routes.SUBJECTS,
            title = stringResource(R.string.bottom_home),
            icon = { Icon(Icons.Default.Home, contentDescription = null) }
        ),
        BottomNavItem(
            route = Routes.GROUPS_TAB,
            title = stringResource(R.string.bottom_students),
            icon = { Icon(Icons.Default.Person, contentDescription = null) }
        ),
        BottomNavItem(
            route = Routes.SEMESTERS,
            title = stringResource(R.string.bottom_semesters),
            icon = { Icon(Icons.Default.DateRange, contentDescription = null) }
        ),
        BottomNavItem(
            route = Routes.SETTINGS_TAB,
            title = stringResource(R.string.bottom_settings),
            icon = { Icon(Icons.Default.Settings, contentDescription = null) }
        )
    )

    val shouldShowBottomBar = currentRoute != null && currentRoute != Routes.WELCOME
    val selectedTopLevelRoute = currentTopLevelRoute(navBackStackEntry?.destination)

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                    tonalElevation = 0.dp
                ) {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = selectedTopLevelRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Routes.SUBJECTS) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = item.icon,
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = fixedBottomLabelSizeSp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.WELCOME) {
                WelcomeScreen(onNameSaved = { name ->
                    viewModel.saveName(name)
                    navController.navigate(Routes.SUBJECTS) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                })
            }

            composable(Routes.SUBJECTS) {
                SubjectScreen(
                    userName = userName ?: "",
                    semesters = semesters,
                    selectedSemesterId = selectedSemesterId,
                    onSemesterSelected = { viewModel.selectSemester(it) },
                    onSubjectClick = { subjectId ->
                        val semesterId = selectedSemesterId ?: semesters.firstOrNull()?.id
                        if (semesterId != null) {
                            navController.navigate(Routes.subjectLessonTypes(semesterId, subjectId))
                        } else {
                            Toast.makeText(context, semesterRequiredMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            composable(
                route = Routes.SUBJECT_LESSON_TYPES,
                arguments = listOf(
                    navArgument("semesterId") { type = NavType.LongType },
                    navArgument("subjectId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val semesterId = backStackEntry.arguments?.getLong("semesterId") ?: 1L
                val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
                SubjectLessonTypeScreen(
                    subjectId = subjectId,
                    onTypeClick = { typeId ->
                        navController.navigate(Routes.groups(semesterId, subjectId, typeId))
                    },
                    onBackClick = {
                        navController.navigate(Routes.SUBJECTS) {
                            popUpTo(Routes.SUBJECTS) { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(
                route = Routes.GROUPS,
                arguments = listOf(
                    navArgument("semesterId") { type = NavType.LongType },
                    navArgument("subjectId") { type = NavType.LongType },
                    navArgument("subjectLessonTypeId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val semesterId = backStackEntry.arguments?.getLong("semesterId") ?: 1L
                val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
                val subjectLessonTypeId = backStackEntry.arguments?.getLong("subjectLessonTypeId") ?: 0L
                GroupScreen(
                    onGroupClick = { groupId ->
                        navController.navigate(Routes.journal(semesterId, groupId, subjectLessonTypeId))
                    },
                    onGroupAnalyticsClick = { groupId ->
                        navController.navigate(Routes.analytics(semesterId, groupId, subjectId))
                    },
                    onBackClick = {
                        navController.navigate(Routes.subjectLessonTypes(semesterId, subjectId)) {
                            popUpTo(Routes.subjectLessonTypes(semesterId, subjectId)) { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Routes.GROUPS_TAB) {
                GroupScreen(
                    onGroupClick = { groupId ->
                        navController.navigate(Routes.students(groupId))
                    },
                    onBackClick = {
                        navController.navigate(Routes.SUBJECTS) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    showBackButton = false
                )
            }

            composable(
                route = Routes.STUDENTS,
                arguments = listOf(navArgument("groupId") { type = NavType.LongType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
                StudentScreen(
                    groupId = groupId,
                    onBackClick = {
                        navController.navigate(Routes.GROUPS_TAB) {
                            popUpTo(Routes.GROUPS_TAB) { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(
                route = Routes.JOURNAL,
                arguments = listOf(
                    navArgument("semesterId") { type = NavType.LongType },
                    navArgument("groupId") { type = NavType.LongType },
                    navArgument("subjectLessonTypeId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val semesterId = backStackEntry.arguments?.getLong("semesterId") ?: 1L
                val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
                val subjectLessonTypeId = backStackEntry.arguments?.getLong("subjectLessonTypeId") ?: 0L
                JournalScreen(
                    groupId = groupId,
                    subjectLessonTypeId = subjectLessonTypeId,
                    semesterId = semesterId,
                    onBack = { navController.popBackStack() },
                    onTopicsClick = {
                        navController.navigate(Routes.lessonTopics(semesterId, groupId, subjectLessonTypeId))
                    }
                )
            }

            composable(
                route = Routes.ANALYTICS,
                arguments = listOf(
                    navArgument("semesterId") { type = NavType.LongType },
                    navArgument("groupId") { type = NavType.LongType },
                    navArgument("subjectId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val semesterId = backStackEntry.arguments?.getLong("semesterId") ?: 1L
                val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
                val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
                AnalyticsScreen(
                    semesterId = semesterId,
                    groupId = groupId,
                    subjectId = subjectId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.HOMEWORKS,
                arguments = listOf(
                    navArgument("lessonId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getLong("lessonId") ?: 0L
                HomeworkScreen(
                    lessonId = lessonId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.LESSON_TOPICS,
                arguments = listOf(
                    navArgument("semesterId") { type = NavType.LongType },
                    navArgument("groupId") { type = NavType.LongType },
                    navArgument("subjectLessonTypeId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val semesterId = backStackEntry.arguments?.getLong("semesterId") ?: 1L
                val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
                val subjectLessonTypeId = backStackEntry.arguments?.getLong("subjectLessonTypeId") ?: 0L
                LessonTopicsScreen(
                    groupId = groupId,
                    subjectLessonTypeId = subjectLessonTypeId,
                    semesterId = semesterId,
                    onBack = { navController.popBackStack() },
                    onHomeworkClick = { lessonId ->
                        navController.navigate(Routes.homeworks(lessonId))
                    }
                )
            }

            composable(Routes.SETTINGS_TAB) {
                SettingsScreen(
                    userName = userName.orEmpty(),
                    biometricEnabled = biometricEnabled,
                    onSaveUserName = { viewModel.updateUserName(it) },
                    onBiometricToggle = { viewModel.requestBiometricToggle(it) },
                    onManageSemesters = { navController.navigate(Routes.SEMESTERS) },
                    onExportDatabase = { uri -> viewModel.exportDatabase(uri) },
                    onImportDatabase = { uri -> viewModel.importDatabase(uri) },
                    onShareDatabase = { viewModel.shareDatabase() }
                )
            }

            composable(Routes.SEMESTERS) {
                SemesterManagementScreen(
                    showBackButton = false,
                    onBackClick = {
                        navController.navigate(Routes.SETTINGS_TAB) {
                            popUpTo(Routes.SETTINGS_TAB) { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

private fun restartApplication(context: android.content.Context) {
    val packageManager = context.packageManager
    val launchIntent = packageManager.getLaunchIntentForPackage(context.packageName) ?: return
    launchIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(launchIntent)
    Runtime.getRuntime().exit(0)
}

private fun currentTopLevelRoute(destination: NavDestination?): String {
    val route = destination?.route ?: return Routes.SUBJECTS

    return when {
        route == Routes.GROUPS_TAB || route == Routes.STUDENTS -> Routes.GROUPS_TAB
        route == Routes.SEMESTERS -> Routes.SEMESTERS
        route == Routes.SETTINGS_TAB -> Routes.SETTINGS_TAB
        else -> Routes.SUBJECTS
    }
}



