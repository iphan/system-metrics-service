package com.qbio.metrics.measurement

import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime


interface MeasurementRepository : CrudRepository<Measurement, Long> {
    fun findByMetricIdAndTimestampAfter(metricId: Int, from: LocalDateTime): List<Measurement>

    fun findByMetricIdAndTimestampBetween(metricId: Int, from: LocalDateTime, to: LocalDateTime): List<Measurement>
}