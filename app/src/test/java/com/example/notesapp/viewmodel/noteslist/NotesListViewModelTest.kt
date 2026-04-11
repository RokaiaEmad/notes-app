package com.example.notesapp.viewmodel.noteslist

import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.usecase.DeleteNoteUseCase
import com.example.notesapp.domain.usecase.GetNoteUseCase
import com.example.notesapp.ui.screens.noteslist.NotesListViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

class NotesListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: NotesListViewModel

    private var getNoteUseCase: GetNoteUseCase = mockk()
    private var deleteNoteUseCase: DeleteNoteUseCase = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    private fun buildViewModel() {
        viewModel = NotesListViewModel(getNoteUseCase, deleteNoteUseCase)
    }


    @Test
    fun `test getNotes with success response then return success`() = runTest {
        val notes = listOf(
            Note(id = 1, title = "Note 1", content = "Content 1", date = 1000L),
            Note(id = 2, title = "Note 2", content = "Content 2", date = 2000L),
        )
        coEvery { getNoteUseCase() } returns flowOf(notes)

        buildViewModel()

        viewModel.uiState.test {

            // skipItems(1)

            val initial = awaitItem()
            assertEquals(false, initial.isLoading)
            assertEquals(emptyList<Note>(), initial.notes)

            val loading = awaitItem()
            assertEquals(true, loading.isLoading)
            assertNull(loading.error)

            val success = awaitItem()
            assertEquals(false, success.isLoading)
            assertEquals(notes, success.notes)
            assertNull(success.error)

            cancelAndIgnoreRemainingEvents()

        }

    }


    @Test
    fun `test getNotes with error response then return error`() = runTest {

        coEvery { getNoteUseCase() } returns flow { throw RuntimeException("DB error") }
        buildViewModel()

        viewModel.uiState.test {
            awaitItem() // initial
            awaitItem() // loading

            val error = awaitItem()
            assertEquals(false, error.isLoading)
            assertEquals(emptyList<Note>(), error.notes)
            assertEquals("DB error", error.error)
            cancelAndIgnoreRemainingEvents()

        }

    }

    @Test
    fun `test deleteNote with success response then return success`() = runTest {

        val note = Note(id = 1, title = "Test Note", content = "Test Content", date = 1000L)

        coEvery { getNoteUseCase() } returns flowOf(emptyList())
        coEvery {deleteNoteUseCase(note)} returns flowOf(Unit)
        buildViewModel()

        viewModel.deleteNote(note)

        viewModel.uiState.test {
            awaitItem()
            val loading=awaitItem()
            assertEquals(true,loading.isLoading)
            assertNull(loading.error)


            val success = awaitItem()
            assertEquals(false, success.isLoading)
            assertNull(success.error)

            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `test deleteNote with error response then return error`() = runTest {

        val note = Note(id = 1, title = "Test Note", content = "Test Content", date = 1000L)

        coEvery { getNoteUseCase() } returns flowOf(emptyList())
        coEvery { deleteNoteUseCase(note) } returns flow { throw RuntimeException("DB error") }

        buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.deleteNote(note)

            val loading = awaitItem()
            assertEquals(true, loading.isLoading)
            assertNull(loading.error)

            val error = awaitItem()
            assertEquals(false, error.isLoading)
            assertEquals("DB error", error.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

}

