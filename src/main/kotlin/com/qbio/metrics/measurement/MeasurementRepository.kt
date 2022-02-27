package com.qbio.metrics.measurement

import org.springframework.data.repository.CrudRepository


interface MeasurementRepository : CrudRepository<Measurement, Long>