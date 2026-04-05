package com.example.notesapp.domain.usecase

import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(note: Note): Flow<Unit> = flow {
        require(note.title.isNotBlank()) { "Subject can't be empty" }
        require(note.content.isNotBlank()) { "Body can't be empty" }
        noteRepository.insertNote(note)
        emit(Unit)
    }.flowOn(Dispatchers.IO)


}