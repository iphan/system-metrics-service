package com.qbio.metrics.collector

import com.qbio.metrics.measurement.MeasurementService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

@Service
class CollectorService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${collector.address}")
    private lateinit var collectorAddress: String

    @Value("\${collector.port}")
    private val collectorPort: Int = 2655

    @Autowired
    private lateinit var collectorParser: CollectorParser

    @Autowired
    private lateinit var measurementService: MeasurementService

    private var connected = false
    private var connection: Socket? = null
    private var reader: Scanner? = null


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
            val measurements = collectorParser.parseLine(line)
            if (measurements == null) {
                log.warn("Couldn't parse line $line")
            } else {
                measurementService.saveMeasurements(measurements)
            }
        }
    }


    fun stopCollection() {
        connected = false
        connection?.close()
        log.info("Collection is stopped")
    }
}

