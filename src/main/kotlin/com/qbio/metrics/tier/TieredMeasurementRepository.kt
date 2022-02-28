package com.qbio.metrics.tier

import com.qbio.metrics.measurement.BinnedResult
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime


interface TieredMeasurementRepository : CrudRepository<TieredMeasurement, Long> {
    fun deleteByTimestampBefore(expirationDate: LocalDateTime)

    fun findByTierAndMetricIdAndTimestampBetween(tier: Tier, metricId: Int, from: LocalDateTime, to: LocalDateTime): List<TieredMeasurement>

    @Query("""SELECT :to AS binEnd,
                     sum(sum) AS sum,
                     max(max) AS max,
                     min(min) AS min,
                     sum(sample_size) AS sampleSize
              FROM tiered_measurements 
              WHERE tier = :tier 
              AND metric_id = :metricId
              AND timestamp BETWEEN :from AND :to""", nativeQuery = true)
    fun findAggregateByTierAndMetricIdAndTimestampBetween(@Param("tier") tier: Tier,
                                                          @Param("metricId") metricId: Int,
                                                          @Param("from") from: LocalDateTime,
                                                          @Param("to") to: LocalDateTime): BinnedResult?


    @Query("""SELECT FROM_UNIXTIME((CEIL(UNIX_TIMESTAMP(timestamp) / (:binMinutes * 60)) * :binMinutes * 60) - 1) AS binEnd,
                     sum(sum) AS sum,
                     max(max) AS max,
                     min(min) AS min,
                     sum(sample_size) AS sampleSize
              FROM tiered_measurements
              WHERE tier = :tier
              AND metric_id = :metricId 
              AND timestamp BETWEEN :from AND :to
              GROUP BY binEnd
              ORDER BY binEnd""", nativeQuery = true)
    fun findBinnedAggregateByTierAndMetricIdAndTimestampBetween(@Param("tier") tier: Tier,
                                                                @Param("metricId") metricId: Int,
                                                                @Param("binMinutes") binMinutes: Int,
                                                                @Param("from") from: LocalDateTime,
                                                                @Param("to") to: LocalDateTime): List<BinnedResult>

}