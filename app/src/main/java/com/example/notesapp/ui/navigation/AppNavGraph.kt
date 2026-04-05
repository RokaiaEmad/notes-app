package com.example.notesapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notesapp.ui.screens.add_edit_note.AddEditNote
import com.example.notesapp.ui.screens.noteslist.NotesList

@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NotesListDestination,
        modifier = modifier
    ) {
        composable<NotesListDestination> {
            NotesList(
                onAddClick = {
                    navController.navigate(AddEditNoteDestination(0))
                },
                onEditClick = { noteId ->
                    navController.navigate(AddEditNoteDestination(noteId))
                }
            )
        }
        composable<AddEditNoteDestination> {
            AddEditNote(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

    }
}