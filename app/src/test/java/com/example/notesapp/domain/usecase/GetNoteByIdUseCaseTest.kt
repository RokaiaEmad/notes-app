package com.example.notesapp.domain.usecase

import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.repository.NoteRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetNoteByIdUseCaseTest {

    private lateinit var getNoteByIdUseCase: GetNoteByIdUseCase
    private var noteRepository: NoteRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    val note = Note(
        id = 1,
        title = "Test Note",
        content = "Test Content",
        date = 100
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        getNoteByIdUseCase = GetNoteByIdUseCase(noteRepository)
    }

    @Test
    fun `test getNoteById with success then return success`() = runTest {

        coEvery { noteRepository.getNoteById(1) } returns note

        var success: Note? = null
        var error: Throwable? = null

        getNoteByIdUseCase(1)
            .catch {
                error = it
            }.collect {
                success = it
            }

        assertNull(error)
        assertNotNull(success)
        coVerify(exactly = 1) {
            noteRepository.getNoteById(1)
        }

    }

    @Test
    fun `test getNoteById repository throws exception then return error`() = runTest {
        coEvery { noteRepository.getNoteById(1) } throws RuntimeException("Not Found")
        var success: Note? = null
        var error: Throwable? = null

        getNoteByIdUseCase(1)
            .catch {
                error = it
            }.collect {
                success = it
            }

        assertNotNull(error)
        assertNull(success)
        assertEquals("Not Found", error!!.message)
        coVerify(exactly = 1) {
            noteRepository.getNoteById(1)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()

    }


}