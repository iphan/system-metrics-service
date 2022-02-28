package com.qbio.metrics.tier

import com.qbio.metrics.measurement.Aggregation
import com.qbio.metrics.measurement.BinnedResult
import com.qbio.metrics.measurement.MeasurementRepository
import com.qbio.metrics.metric.MetricService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TierService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var measurementRepository: MeasurementRepository

    @Autowired
    private lateinit var tieredMeasurementRepository: TieredMeasurementRepository

    @Autowired
    private lateinit var metricService: MetricService

    @Scheduled(cron = "\${minute.tier.cron.schedule}")
    fun minuteLevelTiering() {
        val now = LocalDateTime.now().minusMinutes(1)
        val from = now.withSecond(0).withNano(0)
        val to = now.withSecond(59).withNano(0)

        findAndAggregateMeasurements(Tier.MINUTE, from, to)
    }

    @Scheduled(cron = "\${hour.tier.cron.schedule}")
    fun hourLevelTiering() {
        val now = LocalDateTime.now().minusHours(1)
        val from = now.withMinute(0).withSecond(0).withNano(0)
        val to = now.withMinute(59).withSecond(59).withNano(0)

        findAndAggregateMeasurements(Tier.HOUR, from, to)
    }

    private fun findAndAggregateMeasurements(tier: Tier, from: LocalDateTime, to: LocalDateTime) {
        log.info("Aggregating data for $tier tier from $from to $to")
        val tieredMeasurements = measurementRepository.findByTimestampBetween(from, to)
            .groupBy( { it.metricId }, {it.value} )
            .map { (metricId, values) ->
                TieredMeasurement(tier, metricId, to, values)
            }

        tieredMeasurementRepository.saveAll(tieredMeasurements)
    }


    fun getMeasurementsByTierAndNameAndTimeframe(tier: Tier, name: String, from: LocalDateTime, to: LocalDateTime?): List<TieredMeasurement> {
        val metricId = metricService.metricNames[name] ?: return listOf()

        return tieredMeasurementRepository.findByTierAndMetricIdAndTimestampBetween(tier, metricId, from, to ?: LocalDateTime.now())
    }

    fun getAggregateMeasurementsByTierAndNameAndTimeframe(aggregate: Aggregation, tier: Tier, name: String,
                                                          from: LocalDateTime, nullableTo: LocalDateTime?): BinnedResult? {
        val metricId = metricService.metricNames[name] ?: return null
        val to = nullableTo ?: LocalDateTime.now()
        return tieredMeasurementRepository.findAggregateByTierAndMetricIdAndTimestampBetween(tier, metricId, from, to)
    }

    fun getBinnedAggregateMeasurementsByTierAndNameAndTimeframe(aggregate: Aggregation, binMinutes: Int,
                                                                tier: Tier, name: String,
                                                                from: LocalDateTime, nullableTo: LocalDateTime?): List<BinnedResult> {
        val metricId = metricService.metricNames[name] ?: return listOf()
        val to = nullableTo ?: LocalDateTime.now()
        return tieredMeasurementRepository.findBinnedAggregateByTierAndMetricIdAndTimestampBetween(tier, metricId, binMinutes, from, to)
    }
}
