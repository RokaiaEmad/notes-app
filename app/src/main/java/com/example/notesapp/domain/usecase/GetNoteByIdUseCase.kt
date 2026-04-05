package com.example.notesapp.domain.usecase

import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetNoteByIdUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(id: Int): Flow<Note?> = flow {
        val note = noteRepository.getNoteById(id)
        emit(note)
    }.flowOn(Dispatchers.IO)
}