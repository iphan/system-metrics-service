package com.qbio.metrics.measurement

import com.qbio.metrics.collector.TimedMeasurements
import com.qbio.metrics.metric.MetricService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MeasurementService {
    @Autowired
    private lateinit var measurementRepository: MeasurementRepository

    @Autowired
    private lateinit var metricService: MetricService

    fun saveMeasurements(rawMeasurements: TimedMeasurements) {
        val measurements = metricService.metricFormulas
            .mapNotNull { (metricId, formula) ->
                rawMeasurements.values[formula]?.toDoubleOrNull()?.let {
                    Measurement(metricId, rawMeasurements.timestamp, it)
                }
            }
        measurementRepository.saveAll(measurements)
    }
}