package com.edujournal.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.edujournal.presentation.screen.*
import com.edujournal.presentation.viewmodel.MainViewModel

@Composable
fun AppNavigation(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val userName by viewModel.userName

    val startDestination = if (userName == null) Routes.WELCOME else Routes.SUBJECTS

    NavHost(navController = navController, startDestination = startDestination) {

        // 1. Приветствие
        composable(Routes.WELCOME) {
            WelcomeScreen(onNameSaved = { name ->
                viewModel.saveName(name)
                navController.navigate(Routes.SUBJECTS) {
                    popUpTo(Routes.WELCOME) { inclusive = true }
                }
            })
        }

        // 2. Предметы
        composable(Routes.SUBJECTS) {
            SubjectScreen(
                userName = userName ?: "",
                onSubjectClick = { subjectId ->
                    navController.navigate(Routes.lessonTypes(subjectId))
                }
            )
        }

        // 3. Типы занятий
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

        // 4. Группы
        composable(
            route = Routes.GROUPS,
            arguments = listOf(
                navArgument("subjectId") { type = NavType.LongType },
                navArgument("typeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            GroupScreen(
                onGroupClick = { groupId ->
                    // ТЕПЕРЬ ПЕРЕХОДИМ К СТУДЕНТАМ, А НЕ В ЖУРНАЛ
                    navController.navigate(Routes.students(groupId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 4.5 Студенты
        composable(
            route = Routes.STUDENTS,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
            StudentScreen(
                groupId = groupId,
                onBackClick = { navController.popBackStack() },
                onOpenJournal = {
                    navController.navigate(Routes.journal(groupId))
                }
            )
        }

        // 5. Журнал
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
    }
}