package com.edujournal.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `fromLocalDate converts date to iso string`() {
        val date = LocalDate.of(2026, 5, 5)

        val result = converters.fromLocalDate(date)

        assertEquals("2026-05-05", result)
    }

    @Test
    fun `toLocalDate parses iso string`() {
        val result = converters.toLocalDate("2026-05-05")

        assertEquals(LocalDate.of(2026, 5, 5), result)
    }
}

