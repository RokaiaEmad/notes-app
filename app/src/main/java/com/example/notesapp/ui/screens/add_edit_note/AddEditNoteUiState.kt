package com.example.notesapp.ui.screens.add_edit_note

import com.example.notesapp.domain.model.Note

data class AddEditNoteUiState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val note: Note? = null
)
