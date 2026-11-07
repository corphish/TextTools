package com.corphish.quicktools.repository

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WalkthroughRepositoryImplTest {

    private val context: Context = mockk()
    private val sharedPreferences: SharedPreferences = mockk()
    private val editor: SharedPreferences.Editor = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { context.packageName } returns "com.corphish.quicktools"
        every { context.getSharedPreferences("com.corphish.quicktools_preferences", Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
    }

    private fun createRepository(stored: Map<String, Any?>): WalkthroughRepositoryImpl {
        every { sharedPreferences.all } returns stored
        return WalkthroughRepositoryImpl(context)
    }

    @Test
    fun testCompletedVersions_EmptyWhenNothingStored() {
        assertTrue(createRepository(emptyMap()).completedVersions.value.isEmpty())
    }

    @Test
    fun testCompletedVersions_ReadsOnlyWalkthroughInts() {
        val repository = createRepository(
            mapOf(
                "walkthrough_main" to 2,
                "walkthrough_broken" to "3",
                "appearance" to "DARK",
                "decimal_points" to 4,
            )
        )
        assertEquals(mapOf("main" to 2), repository.completedVersions.value)
    }

    @Test
    fun testMarkCompleted_PersistsAndPublishes() {
        val repository = createRepository(emptyMap())
        repository.markCompleted("main", 3)

        verify { editor.putInt("walkthrough_main", 3) }
        assertEquals(mapOf("main" to 3), repository.completedVersions.value)
    }

    @Test
    fun testMarkCompleted_KeepsOtherKeys() {
        val repository = createRepository(mapOf("walkthrough_eval" to 1))
        repository.markCompleted("main", 1)
        assertEquals(mapOf("eval" to 1, "main" to 1), repository.completedVersions.value)
    }

    @Test
    fun testResetAll_RemovesOnlyWalkthroughEntries() {
        val repository = createRepository(mapOf("walkthrough_main" to 1, "walkthrough_eval" to 2, "appearance" to "DARK"))
        repository.resetAll()

        verify { editor.remove("walkthrough_main") }
        verify { editor.remove("walkthrough_eval") }
        verify(exactly = 0) { editor.remove("appearance") }
        assertTrue(repository.completedVersions.value.isEmpty())
    }
}
