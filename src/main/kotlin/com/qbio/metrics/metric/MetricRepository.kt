package com.qbio.metrics.metric

import org.springframework.data.repository.CrudRepository


interface MetricRepository : CrudRepository<Metric, Int>