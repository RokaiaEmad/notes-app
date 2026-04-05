package com.example.notesapp.ui.screens.noteslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.usecase.DeleteNoteUseCase
import com.example.notesapp.domain.usecase.GetNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    val getNoteUseCase: GetNoteUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesListUiState())
    val uiState: StateFlow<NotesListUiState> = _uiState

    init {
        getNotes()
    }

    fun retry() {
        getNotes()
    }

    fun getNotes() {

        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            getNoteUseCase().onStart {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        error = null
                    )
                }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false, error = e.message
                    )
                }
            }
                .collect { notes ->
                    _uiState.update {
                        it.copy(
                            notes = notes, isLoading = false, error = null
                        )
                    }
                }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            deleteNoteUseCase(note).onStart {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        error = null
                    )
                }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false, error = e.message
                    )
                }
            }.collect {
                _uiState.update {
                    it.copy(
                        isLoading = false, error = null
                    )
                }
            }

        }

    }
}