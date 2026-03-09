package com.edujournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.presentation.viewmodel.GroupViewModel
import com.edujournal.ui.theme.EduJournalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EduJournalTheme {
                val viewModel: GroupViewModel = hiltViewModel()
                val groupsState = viewModel.groups.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting("Студент", modifier = Modifier.padding(bottom = 32.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Замените на ваш ресурс изображения
                            contentDescription = null,
                            modifier = Modifier.size(128.dp)
                        )
                        Text(
                            text = "Количество групп: ${groupsState.value.size}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = {
                                viewModel.addGroup("ПИ-101")
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Добавить группу")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Добро пожаловать, $name!",
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EduJournalTheme {
        Greeting("Студент")
    }
}
