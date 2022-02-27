package com.qbio.metrics.collector

import com.qbio.metrics.measurement.MeasurementService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.Socket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.PostConstruct
import kotlin.concurrent.thread

@Service
class CollectorService {
    companion object {
        const val SEPARATOR = " "
        const val DATE_HEADER = "Date"
        const val TIME_HEADER = "Time"
        val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")
    }
    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${collector.address}")
    private lateinit var collectorAddress: String

    @Value("\${collector.port}")
    private val collectorPort: Int = 2655

    @Value("\${collector.header}")
    private lateinit var headerConfig: String

    private lateinit var headers: List<String>

    @Autowired
    private lateinit var measurementService: MeasurementService

    private var connected = false
    private var connection: Socket? = null
    private var reader: Scanner? = null



    @PostConstruct
    fun init() {
        headers = headerConfig.split(SEPARATOR)
        log.info("Initialized headers $headers")
    }

    fun startCollection() {
        if (connected) {
            log.info("Collector is already on")
            return
        }

        connected = true
        log.info("Starting collector")
        connection = Socket(collectorAddress, collectorPort)
        reader = Scanner(connection!!.getInputStream())
        thread { read() }
    }

    private fun read() {
        while (connected && reader?.hasNextLine() == true) {
            val line = reader!!.nextLine()
            log.debug(line)
            val measurements = parseLine(line)
            if (measurements == null) {
                log.warn("Couldn't parse line $line")
            } else {
                measurementService.saveMeasurements(measurements)
            }
        }
    }

    private fun parseLine(line: String): TimedMeasurements? {

        val measurements = line.split(SEPARATOR)
            .mapIndexedNotNull { index, value ->
                headers.getOrNull(index)?.let { it to value }
            }.toMap()
        val timestamp = parseDateTime(measurements[DATE_HEADER], measurements[TIME_HEADER])

        return timestamp?.let { TimedMeasurements(it, measurements) }
    }

    private fun parseDateTime(date: String?, time: String?): LocalDateTime? {
        return try {
            LocalDateTime.parse("$date $time", DATE_TIME_FORMATTER)
        } catch (_: Exception) {
            null
        }
    }

    fun stopCollection() {
        connected = false
        connection?.close()
        log.info("Collection is stopped")
    }
}

data class TimedMeasurements(val timestamp: LocalDateTime, val values: Map<String, String>)