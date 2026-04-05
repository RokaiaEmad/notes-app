package com.example.notesapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object NotesListDestination

@Serializable
data class AddEditNoteDestination(
    val noteId: Int
)