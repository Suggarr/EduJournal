package com.edujournal.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
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
import com.edujournal.presentation.screen.JournalScreen
import com.edujournal.presentation.screen.LessonTypeScreen
import com.edujournal.presentation.screen.SettingsScreen
import com.edujournal.presentation.screen.StudentScreen
import com.edujournal.presentation.screen.SubjectScreen
import com.edujournal.presentation.screen.WelcomeScreen
import com.edujournal.presentation.viewmodel.MainViewModel

private data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: @Composable () -> Unit
)

@Composable
fun AppNavigation(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val userName by viewModel.userName
    val biometricEnabled by viewModel.biometricEnabled
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val startDestination = if (userName == null) Routes.WELCOME else Routes.SUBJECTS

    val bottomItems = listOf(
        BottomNavItem(
            route = Routes.SUBJECTS,
            title = stringResource(R.string.bottom_home),
            icon = { Icon(Icons.Default.Home, contentDescription = null) }
        ),
        BottomNavItem(
            route = Routes.GROUPS_TAB,
            title = stringResource(R.string.bottom_groups),
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) }
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
                NavigationBar {
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
                            label = { Text(item.title) }
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
                    onSubjectClick = { subjectId ->
                        navController.navigate(Routes.lessonTypes(subjectId))
                    }
                )
            }

            composable(
                route = Routes.LESSON_TYPES,
                arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
            ) { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
                LessonTypeScreen(
                    subjectId = subjectId,
                    onTypeClick = { typeId ->
                        navController.navigate(Routes.groups(subjectId, typeId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.GROUPS,
                arguments = listOf(
                    navArgument("subjectId") { type = NavType.LongType },
                    navArgument("typeId") { type = NavType.LongType }
                )
            ) {
                GroupScreen(
                    onGroupClick = { groupId ->
                        navController.navigate(Routes.journal(groupId))
                    },
                    onBackClick = { navController.popBackStack() }
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
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.JOURNAL,
                arguments = listOf(navArgument("groupId") { type = NavType.LongType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
                JournalScreen(
                    groupId = groupId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.SETTINGS_TAB) {
                SettingsScreen(
                    userName = userName.orEmpty(),
                    biometricEnabled = biometricEnabled,
                    onSaveUserName = { viewModel.updateUserName(it) },
                    onBiometricToggle = { viewModel.setBiometricEnabled(it) }
                )
            }
        }
    }
}

private fun currentTopLevelRoute(destination: NavDestination?): String {
    val route = destination?.route ?: return Routes.SUBJECTS

    return when {
        route == Routes.GROUPS_TAB || route == Routes.STUDENTS -> Routes.GROUPS_TAB
        route == Routes.SETTINGS_TAB -> Routes.SETTINGS_TAB
        else -> Routes.SUBJECTS
    }
}
