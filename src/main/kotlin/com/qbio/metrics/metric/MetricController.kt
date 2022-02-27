package com.qbio.metrics.metric

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController()
class MetricController {
    @Autowired
    private lateinit var metricService: MetricService

    @PostMapping("metrics")
    fun saveMetric(@RequestBody metric: Metric): ResponseEntity<Metric> {
        val newMetric = metricService.addNewMetric(metric)
        return ResponseEntity.ok(newMetric)
    }
}