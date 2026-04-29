package com.example.notesapp.domain.usecase

import com.example.notesapp.domain.model.Note
import com.example.notesapp.domain.repository.NoteRepository
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
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

class UpdateNoteUseCaseTest {

    private lateinit var updateNoteUseCase: UpdateNoteUseCase
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
        updateNoteUseCase = UpdateNoteUseCase(noteRepository)
    }

    @Test
    fun `test updateNote with success then return success`() = runTest {

        coEvery { noteRepository.updateNote(note) } just Runs

        var success: Unit? = null
        var error: Throwable? = null

        updateNoteUseCase(note)
            .catch {
                error = it
            }.collect {
                success = it
            }
        assertNull(error)
        assertNotNull(success)

        coVerify(exactly = 1) {
            noteRepository.updateNote(note)
        }
    }

    @Test
    fun `test updateNote repository throws exception then return error`() = runTest {

        coEvery { noteRepository.updateNote(note) } throws RuntimeException("update Failed")

        var success: Unit? = null
        var error: Throwable? = null

        updateNoteUseCase(note)
            .catch {
                error = it
            }.collect {
                success = it
            }

        assertNotNull(error)
        assertNull(success)
        assertEquals("update Failed", error!!.message)

        coVerify(exactly = 1) {
            noteRepository.updateNote(note)
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }
}