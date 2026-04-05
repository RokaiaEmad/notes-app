package com.example.notesapp.ui.screens.add_edit_note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.usecase.AddNoteUseCase
import com.example.notesapp.domain.usecase.GetNoteByIdUseCase
import com.example.notesapp.domain.usecase.GetNoteUseCase
import com.example.notesapp.domain.usecase.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val addNoteUseCase: AddNoteUseCase,
    val updateNoteUseCase: UpdateNoteUseCase,
    val getNoteByIdUseCase: GetNoteByIdUseCase
) : ViewModel() {

    private val noteId = savedStateHandle.get<Int>("noteId") ?: 0

    private val _uiState = MutableStateFlow(AddEditNoteUiState())
    val uiState: StateFlow<AddEditNoteUiState> = _uiState

    init {
        if (noteId != 0) {
            getNote()
        }
    }

    fun getNote() {
        viewModelScope.launch {
            getNoteByIdUseCase(noteId)
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { note -> _uiState.update { it.copy(isLoading = false, note = note) } }
        }
    }

    fun saveNote(title: String, content: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val note = Note(
                id = noteId,
                title = title,
                content = content,
                date = System.currentTimeMillis()
            )

            val useCase = if (noteId == 0) addNoteUseCase(note) else updateNoteUseCase(note)
            useCase
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
                .collect { _uiState.update { it.copy(isLoading = false, isSaved = true) } }
        }
    }
}