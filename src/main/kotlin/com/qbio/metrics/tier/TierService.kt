package com.qbio.metrics.tier

import com.qbio.metrics.measurement.MeasurementRepository
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

    @Scheduled(cron = "\${minute.tier.cron.schedule}")
    fun minuteLevelTiering() {
        val now = LocalDateTime.now().minusMinutes(1)
        val from = now.withSecond(0).withNano(0)
        val to = now.withSecond(59).withNano(0)
        log.info("Running scheduled task for minute tier from $from to $to")

        val tieredMeasurements = measurementRepository.findByTimestampBetween(from, to)
            .groupBy( { it.metricId }, {it.value} )
            .map { (metricId, values) ->
                TieredMeasurement(Tier.MINUTE, metricId, to, values)
            }

        tieredMeasurementRepository.saveAll(tieredMeasurements)

    }

}
