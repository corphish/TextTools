package com.corphish.quicktools.repository

import android.content.Context
import android.content.SharedPreferences
import com.corphish.quicktools.data.TemplateType
import com.corphish.quicktools.data.TextTemplate
import com.corphish.quicktools.functions.TextTemplateFunctions
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TextTemplateRepositoryImplTest {
    private lateinit var repository: TextTemplateRepositoryImpl
    private val context: Context = mockk()
    private val sharedPreferences: SharedPreferences = mockk()
    private val editor: SharedPreferences.Editor = mockk(relaxed = true)
    private val textTemplateFunctions: TextTemplateFunctions = mockk()

    @Before
    fun setUp() {
        every { context.getSharedPreferences("text_templates", Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        repository = TextTemplateRepositoryImpl(context, textTemplateFunctions)
    }

    @Test
    fun testSaveAndGetTemplates() {
        // Mock getting empty list initially
        every { sharedPreferences.getString("templates", "[]") } returns "[]"
        
        val template = TextTemplate("1", "Name", "Template %s", TemplateType.PLAIN_TEXT)
        repository.saveTemplate(template)
        
        verify { editor.putString("templates", any()) }
    }

    @Test
    fun testMigration_GetTemplatesWithoutType() {
        // Old JSON without type field
        val json = "[{\"id\":\"1\",\"name\":\"N1\",\"template\":\"T1\"}]"
        every { sharedPreferences.getString("templates", "[]") } returns json
        
        val templates = repository.getTemplates()
        assertEquals(1, templates.size)
        assertEquals(TemplateType.PLAIN_TEXT, templates[0].type)
    }

    @Test
    fun testDeleteTemplate() {
        val json = "[{\"id\":\"1\",\"name\":\"N1\",\"template\":\"T1\",\"type\":\"PLAIN_TEXT\"},{\"id\":\"2\",\"name\":\"N2\",\"template\":\"T2\",\"type\":\"PLAIN_TEXT\"}]"
        every { sharedPreferences.getString("templates", "[]") } returns json
        
        repository.deleteTemplate("1")
        
        verify { editor.putString("templates", any()) }
    }

    @Test
    fun testUpdateTemplate() {
        val json = "[{\"id\":\"1\",\"name\":\"N1\",\"template\":\"T1\",\"type\":\"PLAIN_TEXT\"}]"
        every { sharedPreferences.getString("templates", "[]") } returns json
        
        val updated = TextTemplate("1", "N1U", "T1U", TemplateType.PLAIN_TEXT)
        repository.updateTemplate(updated)
        
        verify { editor.putString("templates", any()) }
    }

    @Test
    fun testApplyTemplate() {
        every { textTemplateFunctions.applyTemplate("T", "I", TemplateType.PLAIN_TEXT) } returns "TI"
        val result = repository.applyTemplate("T", "I", TemplateType.PLAIN_TEXT)
        assertEquals("TI", result)
    }

    @Test
    fun testClearAllTemplates() {
        repository.clearAllTemplates()
        verify { editor.remove("templates") }
    }
}
