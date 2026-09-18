package org.phireox.ofa.engine

import org.junit.Assert.assertTrue
import org.junit.Test
import org.phireox.ofa.data.model.ToolRegistry
import org.phireox.ofa.data.model.ToolType

class ToolProcessorTest {

    @Test
    fun `case converter uppercases text`() {
        val tool = ToolRegistry.byId("case_converter")!!
        val result = ToolProcessor.process(tool, "hello", mapOf("mode" to "upper"))
        assertTrue(result is ToolResult.Text && result.value == "HELLO")
    }

    @Test
    fun `word counter returns counts`() {
        val tool = ToolRegistry.byId("word_counter")!!
        val result = ToolProcessor.process(tool, "One two three", emptyMap())
        assertTrue(result is ToolResult.Text)
        val text = (result as ToolResult.Text).value
        assertTrue("Words: 3" in text)
        assertTrue("Characters: 13" in text)
    }

    @Test
    fun `GST calculator computes inclusive tax`() {
        val tool = ToolRegistry.byId("gst_calculator")!!
        val result = ToolProcessor.process(tool, "", mapOf("amount" to "1180", "rate" to "18", "inclusive" to "true"))
        val text = (result as ToolResult.Text).value
        assertTrue("Base:" in text)
        assertTrue("GST" in text)
    }

    @Test
    fun `UUID generator produces UUID`() {
        val tool = ToolRegistry.byId("uuid_generator")!!
        val result = ToolProcessor.process(tool, "", emptyMap())
        val text = (result as ToolResult.Text).value
        assertTrue(text.contains("-"))
    }

    @Test
    fun `password strength rates weak password`() {
        val tool = ToolRegistry.byId("password_strength")!!
        val result = ToolProcessor.process(tool, "123", emptyMap())
        val text = (result as ToolResult.Text).value
        assertTrue("Weak" in text)
    }
}
