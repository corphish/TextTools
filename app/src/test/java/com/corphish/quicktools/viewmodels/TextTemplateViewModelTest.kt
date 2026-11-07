package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.data.TemplateType
import com.corphish.quicktools.data.TextTemplate
import com.corphish.quicktools.data.Walkthrough
import com.corphish.quicktools.data.Walkthroughs
import com.corphish.quicktools.usecases.ApplyTemplateUseCase
import com.corphish.quicktools.usecases.ManageTemplatesUseCase
import com.corphish.quicktools.usecases.WalkthroughUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextTemplateViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TextTemplateViewModel
    private val manageTemplatesUseCase: ManageTemplatesUseCase = mockk(relaxed = true)
    private val applyTemplateUseCase: ApplyTemplateUseCase = mockk(relaxed = true)
    private val walkthroughUseCase: WalkthroughUseCase = mockk()
    private val completedWalkthroughs = mutableSetOf<Walkthrough>()

    @Before
    fun setUp() {
        every { walkthroughUseCase.isPending(any()) } answers { firstArg<Walkthrough>() !in completedWalkthroughs }
        viewModel = TextTemplateViewModel(manageTemplatesUseCase, applyTemplateUseCase, walkthroughUseCase)
    }

    @Test
    fun testLoadTemplates() = runTest {
        val templates = listOf(TextTemplate("1", "Template 1", "Hello %s"))
        every { manageTemplatesUseCase.getTemplates() } returns templates

        viewModel.loadTemplates()

        assertEquals(templates, viewModel.templates.value)
    }

    @Test
    fun testAddTemplate() = runTest {
        viewModel.addTemplate("New Template", "Welcome %s", TemplateType.PLAIN_TEXT)
        verify { manageTemplatesUseCase.saveTemplate(any()) }
        verify { manageTemplatesUseCase.getTemplates() }
    }

    @Test
    fun testUpdateTemplate() = runTest {
        viewModel.updateTemplate("1", "Updated Name", "Updated Template %s", TemplateType.PLAIN_TEXT)
        verify { manageTemplatesUseCase.updateTemplate(any()) }
        verify { manageTemplatesUseCase.getTemplates() }
    }

    @Test
    fun testDeleteTemplate() = runTest {
        viewModel.deleteTemplate("1")
        verify { manageTemplatesUseCase.deleteTemplate("1") }
        verify { manageTemplatesUseCase.getTemplates() }
    }

    @Test
    fun testApplyTemplate() = runTest {
        val template = TextTemplate("1", "Template 1", "Hello %s", TemplateType.PLAIN_TEXT)
        viewModel.setUserInput("Android")
        every { applyTemplateUseCase.execute("Hello %s", "Android", TemplateType.PLAIN_TEXT) } returns "Hello Android"

        val result = viewModel.applyTemplate(template)

        assertEquals("Hello Android", result)
    }

    @Test
    fun testSetUserInput_IsObservable() {
        viewModel.setUserInput("World")
        assertEquals("World", viewModel.userInput.value)
    }

    @Test
    fun testPreviews_FollowTemplatesAndInput() {
        val plain = TextTemplate("1", "Greeting", "Hello %s")
        val url = TextTemplate("2", "Search", "https://example.com/?q=%s", TemplateType.URL)
        every { manageTemplatesUseCase.getTemplates() } returns listOf(plain, url)
        every { applyTemplateUseCase.execute(any(), any(), any()) } answers {
            "${firstArg<String>()}|${secondArg<String>()}|${thirdArg<TemplateType>()}"
        }

        viewModel.loadTemplates()
        viewModel.setUserInput("a b")

        assertEquals(
            mapOf(
                "1" to "Hello %s|a b|PLAIN_TEXT",
                "2" to "https://example.com/?q=%s|a b|URL",
            ),
            viewModel.previews.value,
        )
    }

    @Test
    fun testPreviews_EmptyWithoutTemplates() {
        viewModel.setUserInput("text")
        assertEquals(emptyMap<String, String>(), viewModel.previews.value)
    }

    @Test
    fun testRender() {
        every { applyTemplateUseCase.execute("Hi %s", "you", TemplateType.PLAIN_TEXT) } returns "Hi you"
        assertEquals("Hi you", viewModel.render("Hi %s", "you", TemplateType.PLAIN_TEXT))
    }

    private fun loadWith(vararg templates: TextTemplate) {
        every { manageTemplatesUseCase.getTemplates() } returns templates.toList()
        viewModel.loadTemplates()
    }

    @Test
    fun testManageWalkthroughs_EmptyUntilLoaded() {
        assertEquals(emptyList<Walkthrough>(), viewModel.manageWalkthroughs.value)
    }

    @Test
    fun testManageWalkthroughs_NewUserGetsCreateThenEdit() {
        loadWith()
        assertEquals(listOf(Walkthroughs.TEMPLATE_CREATE, Walkthroughs.TEMPLATE_EDIT), viewModel.manageWalkthroughs.value)
    }

    @Test
    fun testManageWalkthroughs_AfterCreateTipOnlyEdit() {
        completedWalkthroughs += Walkthroughs.TEMPLATE_CREATE
        loadWith(TextTemplate("1", "Greeting", "Hello %s"))
        assertEquals(listOf(Walkthroughs.TEMPLATE_EDIT), viewModel.manageWalkthroughs.value)
    }

    @Test
    fun testManageWalkthroughs_ExistingTemplatesGetCombined() {
        loadWith(TextTemplate("1", "Greeting", "Hello %s"))
        assertEquals(listOf(Walkthroughs.TEMPLATE_MANAGE), viewModel.manageWalkthroughs.value)
    }

    @Test
    fun testManageWalkthroughs_NoneAfterCombined() {
        completedWalkthroughs += Walkthroughs.TEMPLATE_MANAGE
        loadWith(TextTemplate("1", "Greeting", "Hello %s"))
        assertEquals(emptyList<Walkthrough>(), viewModel.manageWalkthroughs.value)
    }

    @Test
    fun testManageWalkthroughs_FirstTemplateAfterCreateTipSwitchesToEdit() {
        loadWith()
        completedWalkthroughs += Walkthroughs.TEMPLATE_CREATE

        viewModel.addTemplate("Greeting", "Hello %s", TemplateType.PLAIN_TEXT)
        loadWith(TextTemplate("1", "Greeting", "Hello %s"))

        assertEquals(listOf(Walkthroughs.TEMPLATE_EDIT), viewModel.manageWalkthroughs.value)
    }
}
