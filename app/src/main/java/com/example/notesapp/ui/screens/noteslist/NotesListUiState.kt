package com.example.notesapp.ui.screens.noteslist

import com.example.notesapp.domain.model.Note

data class NotesListUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
