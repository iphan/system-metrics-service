package com.qbio.metrics.measurement

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime


interface MeasurementRepository : CrudRepository<Measurement, Long> {

    fun findByMetricIdAndTimestampBetween(metricId: Int, from: LocalDateTime, to: LocalDateTime): List<Measurement>

    fun findByTimestampBetween(from: LocalDateTime, to: LocalDateTime): List<Measurement>

    @Query("""SELECT :to AS binEnd,
                     sum(value) AS aggregate,
                     count(1) AS sampleSize
              FROM measurements 
              WHERE metric_id = :metricId AND timestamp BETWEEN :from AND :to""", nativeQuery = true)
    fun findAverageByMetricIdAndTimestampBetween(@Param("metricId") metricId: Int,
                                                 @Param("from") from: LocalDateTime,
                                                 @Param("to") to: LocalDateTime): BinnedResult?

    @Query("""SELECT :to AS binEnd,
                     max(value) AS aggregate,
                     count(1) AS sampleSize
              FROM measurements 
              WHERE metric_id = :metricId AND timestamp BETWEEN :from AND :to""", nativeQuery = true)
    fun findMaxByMetricIdAndTimestampBetween(@Param("metricId") metricId: Int,
                                             @Param("from") from: LocalDateTime,
                                             @Param("to") to: LocalDateTime): BinnedResult?

    @Query("""SELECT :to AS binEnd,
                     min(value) AS aggregate,
                     count(1) AS sampleSize
              FROM measurements 
              WHERE metric_id = :metricId AND timestamp BETWEEN :from AND :to""", nativeQuery = true)
    fun findMinByMetricIdAndTimestampBetween(@Param("metricId") metricId: Int,
                                             @Param("from") from: LocalDateTime,
                                             @Param("to") to: LocalDateTime): BinnedResult?

    @Query("""SELECT FROM_UNIXTIME((CEIL(UNIX_TIMESTAMP(timestamp) / (:binMinutes * 60)) * :binMinutes * 60) - 1) AS binEnd,
                     SUM(value) as aggregate,
                     COUNT(1) as sampleSize
              FROM measurements
              WHERE metric_id = :metricId AND timestamp BETWEEN :from AND :to
              GROUP BY binEnd""", nativeQuery = true)
    fun findBinnedAverageByMetricIdAndTimestampBetween(@Param("metricId") metricId: Int,
                                                       @Param("binMinutes") binMinutes: Int,
                                                       @Param("from") from: LocalDateTime,
                                                       @Param("to") to: LocalDateTime): List<BinnedResult>

    @Query("""SELECT FROM_UNIXTIME((CEIL(UNIX_TIMESTAMP(timestamp) / (:binMinutes * 60)) * :binMinutes * 60) - 1) AS binEnd,
                     MAX(value) as aggregate,
                     COUNT(1) as sampleSize
              FROM measurements
              WHERE metric_id = :metricId AND timestamp BETWEEN :from AND :to
              GROUP BY binEnd""", nativeQuery = true)
    fun findBinnedMaxByMetricIdAndTimestampBetween(@Param("metricId") metricId: Int,
                                                   @Param("binMinutes") binMinutes: Int,
                                                   @Param("from") from: LocalDateTime,
                                                   @Param("to") to: LocalDateTime): List<BinnedResult>

    @Query("""SELECT FROM_UNIXTIME((CEIL(UNIX_TIMESTAMP(timestamp) / (:binMinutes * 60)) * :binMinutes * 60) - 1) AS binEnd,
                     MIN(value) as aggregate,
                     COUNT(1) as sampleSize
              FROM measurements
              WHERE metric_id = :metricId AND timestamp BETWEEN :from AND :to
              GROUP BY binEnd""", nativeQuery = true)
    fun findBinnedMinByMetricIdAndTimestampBetween(@Param("metricId") metricId: Int,
                                                   @Param("binMinutes") binMinutes: Int,
                                                   @Param("from") from: LocalDateTime,
                                                   @Param("to") to: LocalDateTime): List<BinnedResult>

    fun deleteByTimestampBefore(expirationDate: LocalDateTime)
}

interface BinnedResult {
    fun getBinEnd(): LocalDateTime
    fun getAggregate(): Double
    fun getSampleSize(): Long
}

fun BinnedResult.getAverage(): Double {
    return if (getSampleSize() != 0L) getAggregate() / getSampleSize()
    else Double.NaN
}

fun BinnedResult.computeResult(aggregate: Aggregation): Double {
    return when(aggregate) {
        Aggregation.AVG -> getAverage()
        else -> getAggregate()
    }
}