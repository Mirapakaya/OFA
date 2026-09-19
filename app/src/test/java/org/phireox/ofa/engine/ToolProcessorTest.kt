package org.phireox.ofa.engine

import org.junit.Assert.assertTrue
import org.junit.Test
import org.phireox.ofa.data.model.ToolRegistry

class ToolProcessorTest {

    @Test
    fun `case converter uppercases text`() {
        val tool = ToolRegistry.byId("case_converter")!!
        val result = ToolProcessor.process(null, tool, "hello", mapOf("mode" to "upper"))
        assertTrue(result is ToolResult.Text && result.value == "HELLO")
    }

    @Test
    fun `word counter returns counts`() {
        val tool = ToolRegistry.byId("word_counter")!!
        val result = ToolProcessor.process(null, tool, "One two three", emptyMap())
        assertTrue(result is ToolResult.Text)
        val text = (result as ToolResult.Text).value
        assertTrue("Words: 3" in text)
        assertTrue("Characters: 13" in text)
    }

    @Test
    fun `GST calculator computes inclusive tax`() {
        val tool = ToolRegistry.byId("gst_calculator")!!
        val result = ToolProcessor.process(null, tool, "", mapOf("amount" to "1180", "rate" to "18", "inclusive" to "true"))
        val text = (result as ToolResult.Text).value
        assertTrue("Base:" in text)
        assertTrue("GST" in text)
    }

    @Test
    fun `UUID generator produces UUID`() {
        val tool = ToolRegistry.byId("uuid_generator")!!
        val result = ToolProcessor.process(null, tool, "", emptyMap())
        val text = (result as ToolResult.Text).value
        assertTrue(text.contains("-"))
    }

    @Test
    fun `password strength rates weak password`() {
        val tool = ToolRegistry.byId("password_strength")!!
        val result = ToolProcessor.process(null, tool, "123", emptyMap())
        val text = (result as ToolResult.Text).value
        assertTrue("Weak" in text)
    }

    @Test
    fun `discount calculator returns final price`() {
        val tool = ToolRegistry.byId("discount_calculator")!!
        val result = ToolProcessor.process(null, tool, "", mapOf("price" to "100", "discount" to "10"))
        val text = (result as ToolResult.Text).value
        assertTrue("Final price: 90.00" in text)
    }

    @Test
    fun `simple interest calculator returns interest`() {
        val tool = ToolRegistry.byId("simple_interest")!!
        val result = ToolProcessor.process(null, tool, "", mapOf("principal" to "1000", "rate" to "10", "years" to "2"))
        val text = (result as ToolResult.Text).value
        assertTrue("Interest: 200.00" in text)
    }

    @Test
    fun `salary calculator returns monthly net`() {
        val tool = ToolRegistry.byId("salary_calculator")!!
        val result = ToolProcessor.process(null, tool, "", mapOf("annual" to "120000", "deductions" to "10"))
        val text = (result as ToolResult.Text).value
        assertTrue("Monthly net: 9000.00" in text)
    }

    @Test
    fun `cidr calculator returns network and usable hosts`() {
        val tool = ToolRegistry.byId("cidr_calculator")!!
        val result = ToolProcessor.process(null, tool, "", mapOf("cidr" to "192.168.0.0/24"))
        val text = (result as ToolResult.Text).value
        assertTrue("Network:" in text)
        assertTrue("Usable hosts:" in text)
    }

    @Test
    fun `csv deduplicate removes duplicate rows`() {
        val tool = ToolRegistry.byId("csv_deduplicate")!!
        val result = ToolProcessor.process(null, tool, "name,age\nAlice,30\nBob,25\nAlice,30", emptyMap())
        val text = (result as ToolResult.Text).value
        assertTrue(text.lines().size == 3)
    }

    @Test
    fun `csv to sql generates insert statements`() {
        val tool = ToolRegistry.byId("csv_to_sql")!!
        val result = ToolProcessor.process(null, tool, "name,age\nAlice,30\nBob,25", emptyMap())
        val text = (result as ToolResult.Text).value
        assertTrue("CREATE TABLE" in text)
        assertTrue("INSERT INTO" in text)
    }
}
