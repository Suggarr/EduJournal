package com.edujournal.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TextNormalizationTest {

    @Test
    fun `normalizeSpaces trims and collapses whitespace`() {
        val value = "  Ivan   Ivanov\t\tPetrovich  "

        val normalized = value.normalizeSpaces()

        assertEquals("Ivan Ivanov Petrovich", normalized)
    }

    @Test
    fun `normalizeSpacesOrNull returns null for blank`() {
        val value = "   "

        val normalized = value.normalizeSpacesOrNull()

        assertNull(normalized)
    }

    @Test
    fun `normalizeSpacesOrNull returns normalized value for non blank`() {
        val value = "  DB   Systems "

        val normalized = value.normalizeSpacesOrNull()

        assertEquals("DB Systems", normalized)
    }
}

