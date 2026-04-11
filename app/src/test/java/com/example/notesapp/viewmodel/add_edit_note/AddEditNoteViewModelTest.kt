package com.example.notesapp.viewmodel.add_edit_note

import androidx.lifecycle.SavedStateHandle
import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.usecase.AddNoteUseCase
import com.example.notesapp.domain.usecase.GetNoteByIdUseCase
import com.example.notesapp.domain.usecase.UpdateNoteUseCase
import com.example.notesapp.ui.screens.add_edit_note.AddEditNoteViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest


class AddEditNoteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AddEditNoteViewModel
    private var getNoteByIdUseCase: GetNoteByIdUseCase = mockk()
    private var addNoteUseCase: AddNoteUseCase = mockk()
    private var updateNoteUseCase: UpdateNoteUseCase = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }


    private fun buildViewModel(noteId: Int = 0) {
        val savedStateHandle = SavedStateHandle(mapOf("noteId" to noteId))
        viewModel = AddEditNoteViewModel(
            savedStateHandle,
            addNoteUseCase,
            updateNoteUseCase,
            getNoteByIdUseCase
        )
    }

    @Test
    fun `test getNote with success response then return success`() = runTest {
        val note = Note(id = 1, title = "Test Note", content = "Test Content", date = 1000L)
        coEvery { getNoteByIdUseCase(1) } returns flowOf(note)

        buildViewModel(1)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(false, initial.isLoading)
            assertNull(initial.note)

            val loading = awaitItem()
            assertEquals(true, loading.isLoading)

            val success = awaitItem()
            assertEquals(false, success.isLoading)
            assertEquals(note, success.note)
            assertNull(success.error)

            cancelAndIgnoreRemainingEvents()

        }

    }

    @Test
    fun `test getNote with error response then return error`() = runTest {

        coEvery { getNoteByIdUseCase(1) } returns flow { throw RuntimeException("Not Found") }

        buildViewModel(1)

        viewModel.uiState.test {

            awaitItem()
            awaitItem()

            val error = awaitItem()
            assertEquals(false, error.isLoading)
            assertNull(error.note)
            assertEquals("Not Found", error.error)

            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `saveNote in add mode success then isSaved is true`() = runTest {

        coEvery { addNoteUseCase(any()) } returns flowOf(Unit)

        buildViewModel(0)

        viewModel.uiState.test {
            awaitItem()
            viewModel.saveNote("Test Note", "Test Content")
            val loading = awaitItem()
            assertEquals(true, loading.isLoading)
            assertNull(loading.error)

            val success = awaitItem()
            assertEquals(false, success.isLoading)
            assertEquals(true, success.isSaved)
            assertNull(success.error)

            cancelAndIgnoreRemainingEvents()

        }

    }

    @Test
    fun `saveNote in add mode error then error is set in state`() = runTest {
        coEvery { addNoteUseCase(any()) } returns flow { throw RuntimeException("Not Found") }

        buildViewModel(0)

        viewModel.uiState.test {
            awaitItem()
            viewModel.saveNote("Test Note", "Test Content")

            val loading = awaitItem()
            assertEquals(true, loading.isLoading)
            assertNull(loading.error)

            val error = awaitItem()
            assertEquals(false, error.isLoading)
            assertEquals("Not Found", error.error)
            assertEquals(false, error.isSaved)

            cancelAndIgnoreRemainingEvents()

        }

    }

    @Test
    fun `saveNote in update mode success then isSaved is true`() = runTest {
        coEvery { getNoteByIdUseCase(1) } returns flowOf(
            Note(
                id = 1,
                title = "Test Note",
                content = "Test Content",
                date = 1000L
            )
        )
        coEvery { updateNoteUseCase(any()) } returns flowOf(Unit)

        buildViewModel(1)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.saveNote("Test Note", "Test Content")

            val loading = awaitItem()
            assertEquals(true, loading.isLoading)
            assertNull(loading.error)

            val success = awaitItem()
            assertEquals(false, success.isLoading)
            assertEquals(true, success.isSaved)
            assertNull(success.error)

            cancelAndIgnoreRemainingEvents()

        }

    }

    @Test
    fun `saveNote in update mode error then error is set in state`() = runTest {

        coEvery { getNoteByIdUseCase(1) } returns flowOf(
            Note(
                id = 1,
                title = "Test Note",
                content = "Test Content",
                date = 100
            )
        )

        coEvery { updateNoteUseCase(any()) } returns flow { throw RuntimeException("Insertion Failed") }

        buildViewModel(1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()
            viewModel.saveNote("Test Note", "Test Content")

            awaitItem()

            val error = awaitItem()
            assertEquals(false, error.isLoading)
            assertEquals("Insertion Failed", error.error)
            assertEquals(false, error.isSaved)

            cancelAndIgnoreRemainingEvents()

        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

}