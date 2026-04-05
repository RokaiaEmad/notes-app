package com.example.notesapp.data.mapper

import com.example.notesapp.data.local.NoteEntity
import com.example.notesapp.domain.model.Note

fun NoteEntity.toDomain() = Note(
    id = id,
    title = title,
    content = content,
    date = date
)

fun Note.toEntity() = NoteEntity(
    id = id,
    title = title,
    content = content,
    date = date
)

