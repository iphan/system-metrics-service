package com.qbio.metrics.query

import com.qbio.metrics.measurement.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController()
class QueryController {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var measurementService: MeasurementService

    @GetMapping("/measurements/{metricName}")
    fun getRawMeasurements(@PathVariable metricName: String,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: LocalDateTime,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: LocalDateTime?
                           ): ResponseEntity<DataSetDTO> {

        log.info("Requests for $metricName from $from to $to")
        val measurements = measurementService.getMeasurementsByNameAndTimeframe(metricName, from, to)
            .map { DataPoint(it) }
        return if (measurements.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(DataSetDTO(metricName, measurements))
    }

    @GetMapping("/measurements/{metricName}/aggregate/{aggregate}")
    fun getAggregatedMeasurements(@PathVariable metricName: String,
                                  @PathVariable aggregate: String,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: LocalDateTime,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: LocalDateTime?,
    ): ResponseEntity<MeasurementDTO> {

        log.info("Aggregated request for: $metricName from $from to $to")
        val aggregateEnum = try {
            Aggregation.valueOf(aggregate.uppercase())
        } catch (_ : Exception) {
            return ResponseEntity.badRequest().build()
        }
        val result = measurementService.getAggregateMeasurementsByNameAndTimeframe(
            metricName, from, to)

        return if (result == null) ResponseEntity.noContent().build()
        else ResponseEntity.ok(MeasurementDTO(metricName, result.computeResult(aggregateEnum)))
    }

    @GetMapping("/measurements/{metricName}/aggregate/{aggregate}/bin-minutes/{binMinutes}")
    fun getBinnedAggregatedMeasurements(@PathVariable metricName: String,
                                        @PathVariable aggregate: String,
                                        @PathVariable binMinutes: Int,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: LocalDateTime,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: LocalDateTime?,
    ): ResponseEntity<DataSetDTO> {

        log.info("Aggregated request for: $metricName from $from to $to")
        val aggregateEnum = try {
            Aggregation.valueOf(aggregate.uppercase())
        } catch (_ : Exception) {
            return ResponseEntity.badRequest().build()
        }
        val measurements = measurementService.getBinnedAggregateMeasurementsByNameAndTimeframe(
            binMinutes, metricName, from, to)
            .map { DataPoint(it.getBinEnd(), it.computeResult(aggregateEnum)) }

        return if (measurements.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(DataSetDTO(metricName, measurements))
    }
}