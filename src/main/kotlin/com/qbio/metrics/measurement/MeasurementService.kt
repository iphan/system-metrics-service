package com.qbio.metrics.measurement

import com.qbio.metrics.collector.TimedMeasurements
import com.qbio.metrics.metric.MetricService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MeasurementService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var measurementRepository: MeasurementRepository

    @Autowired
    private lateinit var metricService: MetricService

    fun saveMeasurements(rawMeasurements: TimedMeasurements) {
        val measurements = metricService.metricsById
            .mapNotNull { (metricId, metric) ->
                val values = metric.fields
                    .mapNotNull { rawMeasurements.values[it]?.toDoubleOrNull() }
                metric.applyOperand(values)
                    ?.let { Measurement(metricId, rawMeasurements.timestamp, it) }
            }
        val entities = measurementRepository.saveAll(measurements)
        log.info("Saved ${entities.count()} measurements")
    }

    fun getMeasurementsByNameAndTimeframe(name: String, from: LocalDateTime, to: LocalDateTime?): List<Measurement> {
        val metricId = metricService.metricNames[name] ?: return listOf()

        return measurementRepository.findByMetricIdAndTimestampBetween(metricId, from, to ?: LocalDateTime.now())
    }

    fun getAggregateMeasurementsByNameAndTimeframe(name: String,
                                                   from: LocalDateTime, nullableTo: LocalDateTime?): BinnedResult? {
        val metricId = metricService.metricNames[name] ?: return null
        val to = nullableTo ?: LocalDateTime.now()
        return measurementRepository.findAggregateByMetricIdAndTimestampBetween(metricId, from, to)
    }

    fun getBinnedAggregateMeasurementsByNameAndTimeframe(binMinutes: Int, name: String,
                                                         from: LocalDateTime, nullableTo: LocalDateTime?): List<BinnedResult> {
        val metricId = metricService.metricNames[name] ?: return listOf()
        val to = nullableTo ?: LocalDateTime.now()
        return measurementRepository.findBinnedAggregateByMetricIdAndTimestampBetween(metricId, binMinutes, from, to)
    }
}