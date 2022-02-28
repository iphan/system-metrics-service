package com.qbio.metrics.tier

import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime


interface TieredMeasurementRepository : CrudRepository<TieredMeasurement, Long> {
    fun deleteByTimestampBefore(expirationDate: LocalDateTime)
}