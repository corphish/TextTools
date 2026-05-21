package com.corphish.quicktools.usecases

import com.corphish.quicktools.repository.TextRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveTextUseCaseTest {

    private val textRepository: TextRepository = mockk()
    private val useCase = SaveTextUseCase(textRepository)

    @Test
    fun testExecute() {
        val uriString = "content://test"
        val text = "hello"
        every { textRepository.writeText(uriString, text) } returns true

        val result = useCase.execute(uriString, text)

        assertTrue(result)
        verify { textRepository.writeText(uriString, text) }
    }
}
