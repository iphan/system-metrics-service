package com.qbio.metrics.measurement

import com.qbio.metrics.tier.Tier
import com.qbio.metrics.tier.TieredMeasurementRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RetentionService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var measurementRepository: MeasurementRepository

    @Autowired
    private lateinit var tieredMeasurementRepository: TieredMeasurementRepository

    @Value("\${data.retention.in.days}")
    private val retentionInDays: Long = 365

    @Scheduled(cron = "\${data.retention.cron.schedule}")
    fun deleteOldData() {
        val expirationDateTime = LocalDateTime.now().minusDays(retentionInDays)
        log.info("Deleting data older than $expirationDateTime")
        measurementRepository.deleteByTimestampBefore(expirationDateTime)
        tieredMeasurementRepository.deleteByTimestampBefore(expirationDateTime)
    }
}