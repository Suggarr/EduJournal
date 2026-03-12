package com.edujournal.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.edujournal.presentation.viewmodel.JournalViewModel
import com.edujournal.presentation.component.JournalRowView
import com.edujournal.presentation.component.JournalHeader

@Composable
fun JournalScreen(
    groupId: Long,
    viewModel: JournalViewModel = hiltViewModel()
) {

    val state by viewModel.observeJournal(groupId).collectAsState()

    Column {

        JournalHeader(state.lessons)

        LazyColumn {

            items(state.rows) { row ->

                JournalRowView(row)

            }

        }

    }
}