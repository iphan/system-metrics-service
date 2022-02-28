package com.qbio.metrics.tier

import org.springframework.data.repository.CrudRepository


interface TieredMeasurementRepository : CrudRepository<TieredMeasurement, Long>