package com.qbio.metrics.measurement

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime


interface MeasurementRepository : CrudRepository<Measurement, Long> {

    fun findByMetricIdAndTimestampBetween(metricId: Int, from: LocalDateTime, to: LocalDateTime): List<Measurement>

    @Query("SELECT avg(value) from Measurement WHERE metricId=:metricId AND timestamp BETWEEN :from AND :to")
    fun findAverageByMetricIdAndTimestampBetween(@Param("metricId") metricId: Int,
                                                 @Param("from") from: LocalDateTime,
                                                 @Param("to") to: LocalDateTime): Double?

    @Query("SELECT max(value) from Measurement WHERE metricId=:metricId AND timestamp BETWEEN :from AND :to")
    fun findMaxByMetricIdAndTimestampBetween(@Param("metricId") metricId: Int,
                                             @Param("from") from: LocalDateTime,
                                             @Param("to") to: LocalDateTime): Double?

    @Query("SELECT min(value) from Measurement WHERE metricId=:metricId AND timestamp BETWEEN :from AND :to")
    fun findMinByMetricIdAndTimestampBetween(@Param("metricId") metricId: Int,
                                             @Param("from") from: LocalDateTime,
                                             @Param("to") to: LocalDateTime): Double?
}