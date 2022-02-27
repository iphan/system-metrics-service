package com.qbio.metrics.measurement

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController()
class MeasurementController {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var measurementService: MeasurementService

    @GetMapping("/metrics/{metricName}")
    fun getRawMeasurements(@PathVariable metricName: String,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: LocalDateTime,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: LocalDateTime?
                           ): ResponseEntity<List<Measurement>> {

        log.info("Requests: $metricName from $from")
        val measurements = measurementService.getMeasurementsByNameAndTimeframe(metricName, from, to)
        return if (measurements.isEmpty()) ResponseEntity.noContent().build() else ResponseEntity.ok(measurements)
    }
}