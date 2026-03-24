package com.edujournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.edujournal.presentation.screen.JournalScreen
import com.edujournal.presentation.screen.LessonTypeScreen
import com.edujournal.presentation.screen.SubjectScreen
import com.edujournal.presentation.screen.WelcomeScreen
import com.edujournal.presentation.viewmodel.MainViewModel
import com.edujournal.ui.theme.EduJournalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EduJournalTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val userName by viewModel.userName

    // Определяем стартовый экран
    val startDestination = if (userName == null) "welcome" else "subjects"

    NavHost(navController = navController, startDestination = startDestination) {
        
        // 1. Экран приветствия
        composable("welcome") {
            WelcomeScreen(onNameSaved = { name ->
                viewModel.saveName(name)
                navController.navigate("subjects") {
                    popUpTo("welcome") { inclusive = true }
                }
            })
        }

        // 2. Экран предметов
        composable("subjects") {
            SubjectScreen(
                userName = userName ?: "",
                onSubjectClick = { subjectId ->
                    navController.navigate("lesson_types/$subjectId")
                }
            )}

        // 2.5 Экран типов занятий
        composable(
            route = "lesson_types/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
            LessonTypeScreen(
                subjectId = subjectId,
                onTypeClick = { typeId ->
                    navController.navigate("groups/$subjectId/$typeId")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // В MainActivity.kt внутри NavHost

        composable(
            route = "groups/{subjectId}/{typeId}",
            arguments = listOf(
                navArgument("subjectId") { type = NavType.LongType },
                navArgument("typeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
            val typeId = backStackEntry.arguments?.getLong("typeId") ?: 0L

            // Пока показываем заглушку, чтобы не было вылета
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Здесь будет выбор группы для предмета $subjectId и типа $typeId")
            }
        }
        // 3. Экран журнала
        composable(
            route = "journal/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
            // Передаем subjectId (пока используем как groupId для примера)
            JournalScreen(groupId = subjectId)
        }
    }
}