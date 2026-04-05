package com.example.notesapp.ui.screens.noteslist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notesapp.R
import com.example.notesapp.domain.model.Note
import com.example.notesapp.ui.common.ErrorContent
import com.example.notesapp.ui.common.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesList(
    viewModel: NotesListViewModel = hiltViewModel(),
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notes",
                        color = Color.White
                    )
                }
            )
        },
        floatingActionButton = {
            AddButton(onAddClick)
        }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingContent()

            state.error != null -> {
                ErrorContent(state.error.toString()) {
                    viewModel.retry()
                }
            }

            state.notes.isEmpty() -> EmptyState()
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.notes.size) { index ->
                        NoteItem(
                            note = state.notes[index],
                            onEditClick = {
                                onEditClick(state.notes[index].id)
                            }, onDeleteClick = {})
                    }

                }
            }

        }
    }


}

@Composable
fun NoteItem(
    note: Note,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                }
            }
            Text(
                text = note.content,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AddButton(
    onAddClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(72.dp)
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint()
                    val frameworkPaint = paint.asFrameworkPaint()
                    frameworkPaint.isAntiAlias = true
                    frameworkPaint.color = android.graphics.Color.TRANSPARENT
                    frameworkPaint.setShadowLayer(
                        30f,
                        0f,
                        0f,
                        android.graphics.Color.argb(80, 255, 255, 255) // alpha, R, G, B
                    )
                    canvas.drawCircle(
                        center = center,
                        radius = size.minDimension / 2f,
                        paint = paint
                    )
                }
            }
    ) {
        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = Color(0xFF252525),
            modifier = Modifier.size(72.dp),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Note",
                tint = Color.White,
                modifier = Modifier.size(35.dp)
            )
        }
    }
}

@Composable
fun EmptyState(
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.notesemptystate),
            contentDescription = "Empty State",
            modifier = Modifier
                .size(300.dp)
                .offset(y = 24.dp),
            contentScale = ContentScale.Fit

        )
        Text(
            "Create your first note !",
            textAlign = TextAlign.Center
        )
    }

}

