package com.example.notesapp.ui.screens.add_edit_note

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notesapp.R
import com.example.notesapp.ui.common.ErrorContent
import com.example.notesapp.ui.common.LoadingContent

@Composable
fun AddEditNote(
    viewModel: AddEditNoteViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
) {
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var showError by rememberSaveable { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.note) {
        uiState.note?.let { note ->
            title = note.title
            content = note.content
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onBackClick()
    }
    Scaffold(
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingContent()

            uiState.error != null -> {
                ErrorContent(uiState.error.toString()) {
                    if (uiState.note != null) viewModel.saveNote(title, content)
                    else viewModel.getNote()
                }
            }

            else ->

                Column(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    Header(onBackClick, onSaveClick = {
                        if (title.isBlank() || content.isBlank()) {
                            showError = true
                        } else {
                            viewModel.saveNote(title, content)
                        }
                    })
                    TitleTextField(title) { title = it }
                    ContentTextField(content) { content = it }
                    if (showError) {
                        ErrorMessage()
                    }
                }
        }
    }
}

@Composable
private fun ErrorMessage() {
    Text(
        text = stringResource(R.string.title_and_content_are_required),
        color = Color.Red,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        textAlign = TextAlign.End
    )
}

@Composable
private fun ContentTextField(
    content: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = content,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.type_something),
                fontSize = 20.sp,
                color = Color.Gray
            )
        },
        textStyle = TextStyle(
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}

@Composable
private fun TitleTextField(
    title: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = title,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.title),
                fontSize = 28.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        },
        textStyle = TextStyle(
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun Header(onBackClick: () -> Unit, onSaveClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(onBackClick)

        Spacer(modifier = Modifier.weight(1f))

        SaveButton(onSaveClick = onSaveClick)
    }
}

@Composable
private fun BackButton(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onBackClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back),
            tint = Color.White
        )
    }
}

@Composable
private fun SaveButton(
    onSaveClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onSaveClick()
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Done,
            contentDescription = stringResource(R.string.save),
            tint = Color.White
        )
    }
}