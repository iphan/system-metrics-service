package com.qbio.metrics.collector

import org.slf4j.LoggerFactory
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
    private var collectorPort: Int = 2655

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
            log.info(line)
        }
    }

    fun stopCollection() {
        connected = false
        connection?.close()
        log.info("Collection is stopped")
    }
}