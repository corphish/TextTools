package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.data.Walkthrough
import com.corphish.quicktools.data.WalkthroughStep
import com.corphish.quicktools.usecases.WalkthroughUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WalkthroughViewModelTest {

    private val useCase: WalkthroughUseCase = mockk(relaxed = true)
    private val pending = mutableSetOf<String>()
    private lateinit var viewModel: WalkthroughViewModel

    private val main = Walkthrough(
        key = "main",
        steps = listOf(step("a"), step("b"), step("c")),
    )
    private val extra = Walkthrough(key = "extra", steps = listOf(step("d")))
    private val manual = Walkthrough(key = "manual", steps = listOf(step("a")), autoStart = false)
    private val allTargets = setOf("a", "b", "c", "d")

    private fun step(target: String) = WalkthroughStep(target, 1, 2)

    @Before
    fun setUp() {
        every { useCase.isPending(any()) } answers { firstArg<Walkthrough>().key in pending }
        every { useCase.markCompleted(any()) } answers { pending -= firstArg<Walkthrough>().key }
        pending += setOf("main", "extra", "manual")
        viewModel = WalkthroughViewModel(useCase)
    }

    @Test
    fun testDoesNotStartUntilAllTargetsAreOnScreen() {
        viewModel.setWalkthroughs(listOf(main))
        assertNull(viewModel.state.value)

        viewModel.onTargetsChanged(setOf("a", "b"))
        assertNull(viewModel.state.value)

        viewModel.onTargetsChanged(allTargets)
        assertEquals("main", viewModel.state.value?.walkthrough?.key)
        assertEquals(0, viewModel.state.value?.stepIndex)
    }

    @Test
    fun testDoesNotAutoStartCompletedWalkthrough() {
        pending -= "main"
        viewModel.setWalkthroughs(listOf(main))
        viewModel.onTargetsChanged(allTargets)
        assertNull(viewModel.state.value)
    }

    @Test
    fun testDoesNotAutoStartManualWalkthrough() {
        viewModel.setWalkthroughs(listOf(manual))
        viewModel.onTargetsChanged(allTargets)
        assertNull(viewModel.state.value)
    }

    @Test
    fun testNextWalksThroughStepsAndCompletes() {
        viewModel.setWalkthroughs(listOf(main))
        viewModel.onTargetsChanged(allTargets)

        val first = viewModel.state.value!!
        assertFalse(first.hasPrevious)
        assertTrue(first.hasNext)

        viewModel.next()
        viewModel.next()
        val last = viewModel.state.value!!
        assertEquals(2, last.stepIndex)
        assertTrue(last.hasPrevious)
        assertFalse(last.hasNext)

        viewModel.next()
        assertNull(viewModel.state.value)
        verify(exactly = 1) { useCase.markCompleted(main) }
    }

    @Test
    fun testBackReturnsToPreviousStep() {
        viewModel.setWalkthroughs(listOf(main))
        viewModel.onTargetsChanged(allTargets)
        viewModel.next()
        viewModel.back()
        assertEquals(0, viewModel.state.value?.stepIndex)

        viewModel.back()
        assertEquals(0, viewModel.state.value?.stepIndex)
    }

    @Test
    fun testSkipCompletesWalkthrough() {
        viewModel.setWalkthroughs(listOf(main))
        viewModel.onTargetsChanged(allTargets)
        viewModel.skip()

        assertNull(viewModel.state.value)
        verify { useCase.markCompleted(main) }
    }

    @Test
    fun testNextSkipsStepsWhoseTargetLeftTheScreen() {
        viewModel.setWalkthroughs(listOf(main))
        viewModel.onTargetsChanged(allTargets)
        viewModel.onTargetsChanged(setOf("a", "c"))

        assertTrue(viewModel.state.value!!.hasNext)
        viewModel.next()
        assertEquals(2, viewModel.state.value?.stepIndex)
    }

    @Test
    fun testCurrentStepStaysWhenItsTargetIsMissing() {
        viewModel.setWalkthroughs(listOf(main))
        viewModel.onTargetsChanged(allTargets)
        viewModel.onTargetsChanged(emptySet())

        assertEquals("main", viewModel.state.value?.walkthrough?.key)
        assertEquals(0, viewModel.state.value?.stepIndex)
        assertFalse(viewModel.state.value!!.hasNext)
    }

    @Test
    fun testWalkthroughsRunOneAfterAnotherInOrder() {
        viewModel.setWalkthroughs(listOf(main, extra))
        viewModel.onTargetsChanged(allTargets)
        assertEquals("main", viewModel.state.value?.walkthrough?.key)

        viewModel.skip()
        assertEquals("extra", viewModel.state.value?.walkthrough?.key)

        viewModel.skip()
        assertNull(viewModel.state.value)
    }

    @Test
    fun testStart_ShowsCompletedWalkthroughAgain() {
        pending.clear()
        viewModel.setWalkthroughs(listOf(main))
        viewModel.onTargetsChanged(allTargets)
        assertNull(viewModel.state.value)

        assertTrue(viewModel.start("main"))
        assertEquals("main", viewModel.state.value?.walkthrough?.key)

        viewModel.skip()
        assertNull(viewModel.state.value)
    }

    @Test
    fun testStart_ManualWalkthroughWaitsForTargets() {
        viewModel.setWalkthroughs(listOf(manual))
        assertTrue(viewModel.start("manual"))
        assertNull(viewModel.state.value)

        viewModel.onTargetsChanged(allTargets)
        assertEquals("manual", viewModel.state.value?.walkthrough?.key)
    }

    @Test
    fun testStart_RequestedWalkthroughGoesBeforeAutoOnes() {
        viewModel.setWalkthroughs(listOf(main, manual))
        viewModel.start("manual")
        viewModel.onTargetsChanged(allTargets)
        assertEquals("manual", viewModel.state.value?.walkthrough?.key)

        viewModel.skip()
        assertEquals("main", viewModel.state.value?.walkthrough?.key)
    }

    @Test
    fun testStart_QueuesBehindShownWalkthrough() {
        viewModel.setWalkthroughs(listOf(main, manual))
        viewModel.onTargetsChanged(allTargets)
        viewModel.start("manual")
        assertEquals("main", viewModel.state.value?.walkthrough?.key)

        viewModel.next()
        viewModel.next()
        viewModel.next()
        assertEquals("manual", viewModel.state.value?.walkthrough?.key)
    }

    @Test
    fun testStart_ShownWalkthroughKeepsItsStep() {
        viewModel.setWalkthroughs(listOf(main))
        viewModel.onTargetsChanged(allTargets)
        viewModel.next()

        assertTrue(viewModel.start("main"))
        assertEquals(1, viewModel.state.value?.stepIndex)
    }

    @Test
    fun testStart_UnknownKey() {
        viewModel.setWalkthroughs(listOf(main))
        assertFalse(viewModel.start("unknown"))
    }

    @Test
    fun testActionsWithoutWalkthroughDoNothing() {
        viewModel.next()
        viewModel.back()
        viewModel.skip()
        assertNull(viewModel.state.value)
        verify(exactly = 0) { useCase.markCompleted(any()) }
    }
}
