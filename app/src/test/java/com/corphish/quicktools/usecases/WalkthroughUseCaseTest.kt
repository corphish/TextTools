package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.Walkthrough
import com.corphish.quicktools.data.WalkthroughStep
import com.corphish.quicktools.repository.WalkthroughRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WalkthroughUseCaseTest {

    private val repository: WalkthroughRepository = mockk(relaxed = true)
    private val completed = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val useCase = WalkthroughUseCase(repository)

    private fun walkthrough(version: Int = 1) =
        Walkthrough(key = "main", steps = listOf(WalkthroughStep("target", 1, 2)), version = version)

    @Before
    fun setUp() {
        every { repository.completedVersions } returns completed
    }

    @Test
    fun testIsPending_NeverCompleted() {
        assertTrue(useCase.isPending(walkthrough()))
    }

    @Test
    fun testIsPending_CompletedAtSameVersion() {
        completed.value = mapOf("main" to 1)
        assertFalse(useCase.isPending(walkthrough()))
    }

    @Test
    fun testIsPending_CompletedAtOlderVersion() {
        completed.value = mapOf("main" to 1)
        assertTrue(useCase.isPending(walkthrough(version = 2)))
    }

    @Test
    fun testIsPending_CompletedAtNewerVersion() {
        completed.value = mapOf("main" to 3)
        assertFalse(useCase.isPending(walkthrough(version = 2)))
    }

    @Test
    fun testMarkCompleted_SavesCurrentVersion() {
        useCase.markCompleted(walkthrough(version = 4))
        verify { repository.markCompleted("main", 4) }
    }

    @Test
    fun testResetAll() {
        useCase.resetAll()
        verify { repository.resetAll() }
    }
}
