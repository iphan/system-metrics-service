package com.qbio.metrics.measurement

import com.qbio.metrics.collector.TimedMeasurements
import com.qbio.metrics.metric.MetricService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

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

    fun getMeasurementsByNameAndTimeframe(name: String, from: LocalDateTime, to: LocalDateTime?): List<Measurement> {
        val metricId = metricService.metricNames[name] ?: return listOf()

        return measurementRepository.findByMetricIdAndTimestampBetween(metricId, from, to ?: LocalDateTime.now())
    }

    fun getAggregateMeasurementsByNameAndTimeframe(aggregate: Aggregation, name: String,
                                                   from: LocalDateTime, nullableTo: LocalDateTime?): Double? {
        val metricId = metricService.metricNames[name] ?: return null
        val to = nullableTo ?: LocalDateTime.now()
        return when (aggregate) {
            Aggregation.AVG -> measurementRepository.findAverageByMetricIdAndTimestampBetween(metricId, from, to)
            Aggregation.MAX -> measurementRepository.findMaxByMetricIdAndTimestampBetween(metricId, from, to)
            Aggregation.MIN -> measurementRepository.findMinByMetricIdAndTimestampBetween(metricId, from, to)
        }
    }

    fun getBinnedAggregateMeasurementsByNameAndTimeframe(aggregate: Aggregation, binMinutes: Int, name: String,
                                                         from: LocalDateTime, nullableTo: LocalDateTime?): List<BinnedResult> {
        val metricId = metricService.metricNames[name] ?: return listOf()
        val to = nullableTo ?: LocalDateTime.now()
        return when (aggregate) {
            Aggregation.AVG -> measurementRepository.findBinnedAverageByMetricIdAndTimestampBetween(metricId, binMinutes, from, to)
            Aggregation.MAX -> measurementRepository.findBinnedMaxByMetricIdAndTimestampBetween(metricId, binMinutes, from, to)
            Aggregation.MIN -> measurementRepository.findBinnedMinByMetricIdAndTimestampBetween(metricId, binMinutes, from, to)
        }
    }
}