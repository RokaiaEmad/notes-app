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

class AddNoteUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var addNoteUseCase: AddNoteUseCase
    private var noteRepository: NoteRepository = mockk()

    private val validNote = Note(
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
        addNoteUseCase = AddNoteUseCase(noteRepository)
    }

    @Test
    fun `test addNote with valid note then return success`() = runTest {
        coEvery { noteRepository.insertNote(validNote) } just Runs

        var success: Unit? = null
        var error: Throwable? = null

        addNoteUseCase(validNote)
            .catch { error = it }
            .collect { success = it }

        assertNotNull(success)
        assertNull(error)

        coVerify(exactly = 1) {
            noteRepository.insertNote(validNote)
        }


    }

    @Test
    fun `test addNote with empty title then return error`() = runTest {

        val invalidNote = validNote.copy(title = "")
        var success: Unit? = null
        var error: Throwable? = null


        addNoteUseCase(invalidNote)
            .catch {
                error = it
            }.collect {
                success = it
            }

        assertNotNull(error)
        assertNull(success)
        assertEquals("Subject can't be empty", error!!.message)
        coVerify(exactly = 0) {
            noteRepository.insertNote(any())
        }
    }

    @Test
    fun `test addNote with empty content then return error`() = runTest {
        val invalidNote = validNote.copy(content = "")

        var success: Unit? = null
        var error: Throwable? = null

        addNoteUseCase(invalidNote)
            .catch {
                error = it
            }.collect {
                success = it
            }

        assertNotNull(error)
        assertNull(success)
        assertEquals("Body can't be empty", error!!.message)
        coVerify(exactly = 0) {
            noteRepository.insertNote(any())
        }

    }

    @Test
    fun `test addNote repository throws exception then return error`()=runTest {
        coEvery { noteRepository.insertNote(validNote) } throws RuntimeException("Insertion Failed")
        var success: Unit? = null
        var error: Throwable? = null

        addNoteUseCase(validNote)
            .catch {
                error = it
            }.collect {
                success = it
            }

        assertNotNull(error)
        assertNull(success)
        assertEquals("Insertion Failed", error!!.message)

        coVerify(exactly = 1) {
            noteRepository.insertNote(validNote)
        }
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()

    }
}