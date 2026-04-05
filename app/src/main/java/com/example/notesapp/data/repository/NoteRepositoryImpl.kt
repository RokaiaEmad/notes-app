package com.example.notesapp.data.repository

import com.example.notesapp.data.local.NoteDao
import com.example.notesapp.data.mapper.toDomain
import com.example.notesapp.data.mapper.toEntity
import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getNotes(): Flow<List<Note>> {
        return noteDao.getNotes().map { notes ->
            notes.map { it.toDomain() }
        }
    }

    override suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)?.toDomain()
    }

    override suspend fun insertNote(note: Note) {
        noteDao.insertNote(note.toEntity())
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toEntity())
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toEntity())
    }

}