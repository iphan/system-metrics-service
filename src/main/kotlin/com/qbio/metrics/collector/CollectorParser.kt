package com.qbio.metrics.collector

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

@Component
class CollectorParser {
    companion object {
        const val SEPARATOR = " "
        const val DATE_HEADER = "Date"
        const val TIME_HEADER = "Time"
        val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${collector.header}")
    private lateinit var headerConfig: String

    final lateinit var headers: List<String>
        private set

    @PostConstruct
    fun init() {
        headers = headerConfig.split(SEPARATOR)
        log.info("Initialized headers $headers")
    }

    fun parseLine(line: String): TimedMeasurements? {

        val measurements = line.split(SEPARATOR)
            .mapIndexedNotNull { index, value ->
                headers.getOrNull(index)?.let { it to value }
            }.toMap()
        val timestamp = parseDateTime(measurements[DATE_HEADER], measurements[TIME_HEADER])

        return timestamp?.let { TimedMeasurements(it, measurements) }
    }

    fun parseDateTime(date: String?, time: String?): LocalDateTime? {
        return try {
            LocalDateTime.parse("$date $time", DATE_TIME_FORMATTER)
        } catch (_: Exception) {
            null
        }
    }

    fun setHeaderConfig(config: String) {
        headerConfig = config
    }
}

data class TimedMeasurements(val timestamp: LocalDateTime, val values: Map<String, String>)