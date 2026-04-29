package com.example.notesapp.domain.usecase

import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.repository.NoteRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import io.mockk.coVerify
import kotlinx.coroutines.flow.flow


class GetNoteUseCaseTest {

    private lateinit var getNoteUseCase: GetNoteUseCase
    private var noteRepository: NoteRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    val notes = listOf<Note>(
        Note(
            id = 1,
            title = "Test Note",
            content = "Test Content",
            date = 100
        ),
        Note(
            id = 2,
            title = "Test Note 2",
            content = "Test Content 2",
            date = 102
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        getNoteUseCase = GetNoteUseCase(noteRepository)
    }

    @Test
    fun `test getNotes with success response then return success`() = runTest {

        coEvery { noteRepository.getNotes() } returns flowOf(notes)

        var success: List<Note>? = null
        var error: Throwable? = null

        getNoteUseCase()
            .catch {
                error = it
            }.collect {
                success = it
            }

        assertNull(error)
        assertEquals(notes, success)
    }

    @Test
    fun `test getNotes with error response then return error`() = runTest {
        coEvery { noteRepository.getNotes() } returns flow {
            throw RuntimeException("Empty List")
        }
        var success: List<Note>? = null
        var error: Throwable? = null

        getNoteUseCase()
            .catch {
                error = it
            }.collect {
                success = it
            }

        assertEquals("Empty List", error!!.message)
        assertNull(success)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()

    }


}