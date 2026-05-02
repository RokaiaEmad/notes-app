package com.example.notesapp.data.mapper

import com.example.notesapp.data.local.NoteEntity
import junit.framework.TestCase.assertEquals
import org.junit.Test

class NoteMapperTest {


    @Test
    fun `toDomain maps all fields correctly`() {
        val noteEntity = NoteEntity(
            id = 1,
            title = "Test Note",
            content = "Test Content",
            date = 100
        )
        val note = noteEntity.toDomain()
        assertEquals(noteEntity.id, note.id)
        assertEquals(noteEntity.title, note.title)
        assertEquals(noteEntity.content, note.content)
        assertEquals(noteEntity.date, note.date)
    }

}