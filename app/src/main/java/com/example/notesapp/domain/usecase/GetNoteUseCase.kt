package com.example.notesapp.domain.usecase

import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> = noteRepository.getNotes()
}
