package com.corphish.quicktools.usecases

import com.corphish.quicktools.repository.TextTemplateRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ApplyTemplateUseCaseTest {
    private lateinit var useCase: ApplyTemplateUseCase
    private val repository: TextTemplateRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        useCase = ApplyTemplateUseCase(repository)
    }

    @Test
    fun testExecute() {
        every { repository.applyTemplate("Hello %s", "World") } returns "Hello World"
        val result = useCase.execute("Hello %s", "World")
        assertEquals("Hello World", result)
    }
}
