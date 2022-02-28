package com.qbio.metrics.collector

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CollectorParserTest {
    companion object {
        const val TEST_HEADERS = "Date Time [CPU]User% [CPU]Nice% [CPU]Sys%"
    }

    private val sut = CollectorParser()

    @BeforeEach
    fun init() {
        sut.setHeaderConfig(TEST_HEADERS)
        sut.init()
    }

    @Nested
    inner class InitMethod {
        @Test
        fun `should parse header config`() {
            assertEquals(TEST_HEADERS.split(CollectorParser.SEPARATOR), sut.headers)
        }
    }

    @Nested
    inner class ParseLineMethod {
        @Test
        fun `should return null for invalid line`() {
            val actual = sut.parseLine("random string")

            assertNull(actual)
        }

        @Test
        fun `should return null for line with invalid timestamp`() {
            val actual = sut.parseLine("2022-02-27 12:38:45 1 2 3 4")

            assertNull(actual)
        }

        @Test
        fun `should parse line with fewer values`() {
            val actual = sut.parseLine("20220227 12:38:45 1 2")

            val expected = TimedMeasurements(
                LocalDateTime.of(2022, 2, 27, 12, 38, 45),
                mapOf(
                    "Date" to "20220227",
                    "Time" to "12:38:45",
                    "[CPU]User%" to "1",
                    "[CPU]Nice%" to "2")
            )
            assertEquals(expected, actual)
        }

        @Test
        fun `should parse valid line and drop extra values`() {
            val actual = sut.parseLine("20220227 12:38:45 1 2 3 4")

            val expected = TimedMeasurements(
                LocalDateTime.of(2022, 2, 27, 12, 38, 45),
                mapOf(
                    "Date" to "20220227",
                    "Time" to "12:38:45",
                    "[CPU]User%" to "1",
                    "[CPU]Nice%" to "2",
                    "[CPU]Sys%" to "3")
            )
            assertEquals(expected, actual)
        }
    }

    @Nested
    inner class ParseDateTimeMethod {
        @Test
        fun `should return null if invalid format`() {
            val actual = sut.parseDateTime("invalid", "format")

            assertNull(actual)
        }

        @Test
        fun `should parse date`() {
            val actual = sut.parseDateTime("20220227", "12:38:45")

            val expected = LocalDateTime.of(2022, 2, 27, 12, 38, 45)
            assertEquals(expected, actual)
        }
    }
}