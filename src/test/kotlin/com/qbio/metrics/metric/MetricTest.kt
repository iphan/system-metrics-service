package com.qbio.metrics.metric

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MetricTest {

    @Nested
    inner class InitMethod {
        @Test
        fun `should parse formula without operand`() {
            //given
            val sut = Metric("name", " field1 ")

            //when
            sut.init()

            //then
            assertEquals(listOf(sut.formula.trim()), sut.fields)
        }

        @Test
        fun `should parse formula with plus operand`() {
            testOperandParsing("+")
        }

        @Test
        fun `should parse formula with minus operand`() {
            testOperandParsing("-")
        }

        @Test
        fun `should parse formula with multiply operand`() {
            testOperandParsing("*")
        }

        @Test
        fun `should parse formula with divide operand`() {
            testOperandParsing("/")
        }

        private fun testOperandParsing(operand: String) {
            //given
            val fields = (1..3).map { "field_$it" }
            val sut = Metric("name", fields.joinToString(operand ))

            //when
            sut.init()

            //then
            assertEquals(fields, sut.fields)
        }
    }
}