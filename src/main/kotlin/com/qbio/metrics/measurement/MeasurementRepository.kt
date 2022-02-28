package com.qbio.metrics.measurement

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime


interface MeasurementRepository : CrudRepository<Measurement, Long> {

    fun findByMetricIdAndTimestampBetween(metricId: Int, from: LocalDateTime, to: LocalDateTime): List<Measurement>

    fun findByTimestampBetween(from: LocalDateTime, to: LocalDateTime): List<Measurement>

    @Query("""SELECT :to AS binEnd,
                     sum(value) AS sum,
                     max(value) AS max,
                     min(value) AS min,
                     count(1) AS sampleSize
              FROM measurements 
              WHERE metric_id = :metricId AND timestamp BETWEEN :from AND :to""", nativeQuery = true)
    fun findAggregateByMetricIdAndTimestampBetween(@Param("metricId") metricId: Int,
                                                   @Param("from") from: LocalDateTime,
                                                   @Param("to") to: LocalDateTime): BinnedResult?


    @Query("""SELECT FROM_UNIXTIME((CEIL(UNIX_TIMESTAMP(timestamp) / (:binMinutes * 60)) * :binMinutes * 60) - 1) AS binEnd,
                     sum(value) AS sum,
                     max(value) AS max,
                     min(value) AS min,
                     COUNT(1) as sampleSize
              FROM measurements
              WHERE metric_id = :metricId AND timestamp BETWEEN :from AND :to
              GROUP BY binEnd
              ORDER BY binEnd""", nativeQuery = true)
    fun findBinnedAggregateByMetricIdAndTimestampBetween(@Param("metricId") metricId: Int,
                                                         @Param("binMinutes") binMinutes: Int,
                                                         @Param("from") from: LocalDateTime,
                                                         @Param("to") to: LocalDateTime): List<BinnedResult>

    fun deleteByTimestampBefore(expirationDate: LocalDateTime)
}

interface BinnedResult {
    fun getBinEnd(): LocalDateTime
    fun getSum(): Double
    fun getMax(): Double
    fun getMin(): Double
    fun getSampleSize(): Long
}

fun BinnedResult.getAverage(): Double {
    return if (getSampleSize() != 0L) getSum() / getSampleSize()
    else Double.NaN
}

fun BinnedResult.computeResult(aggregate: Aggregation): Double {
    return when(aggregate) {
        Aggregation.AVG -> getAverage()
        Aggregation.MAX -> getMax()
        Aggregation.MIN -> getMin()
    }
}