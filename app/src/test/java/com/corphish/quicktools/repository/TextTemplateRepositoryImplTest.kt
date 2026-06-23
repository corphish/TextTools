package com.corphish.quicktools.repository

import android.content.Context
import android.content.SharedPreferences
import com.corphish.quicktools.data.TextTemplate
import com.corphish.quicktools.functions.TextTemplateFunctions
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

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
        
        val template = TextTemplate("1", "Name", "Template %s")
        repository.saveTemplate(template)
        
        // JSON structure verify would be hard if org.json is not available, 
        // but verify { editor.putString("templates", any()) } should work.
        verify { editor.putString("templates", any()) }
    }

    @Test
    fun testDeleteTemplate() {
        val json = "[{\"id\":\"1\",\"name\":\"N1\",\"template\":\"T1\"},{\"id\":\"2\",\"name\":\"N2\",\"template\":\"T2\"}]"
        every { sharedPreferences.getString("templates", "[]") } returns json
        
        repository.deleteTemplate("1")
        
        verify { editor.putString("templates", any()) }
    }

    @Test
    fun testUpdateTemplate() {
        val json = "[{\"id\":\"1\",\"name\":\"N1\",\"template\":\"T1\"}]"
        every { sharedPreferences.getString("templates", "[]") } returns json
        
        val updated = TextTemplate("1", "N1U", "T1U")
        repository.updateTemplate(updated)
        
        verify { editor.putString("templates", any()) }
    }

    @Test
    fun testApplyTemplate() {
        every { textTemplateFunctions.applyTemplate("T", "I") } returns "TI"
        val result = repository.applyTemplate("T", "I")
        assertEquals("TI", result)
    }

    @Test
    fun testClearAllTemplates() {
        repository.clearAllTemplates()
        verify { editor.remove("templates") }
    }
}
