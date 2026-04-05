package com.example.notesapp.domain.model

data class Note(
    val id: Int = 0,
    val title: String,
    val content: String,
    val date: Long,
)
