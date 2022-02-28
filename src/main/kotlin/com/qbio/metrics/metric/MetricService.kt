package com.qbio.metrics.metric

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class MetricService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var metricRepository: MetricRepository

    final lateinit var metricsById: Map<Int, Metric>
        private set

    final lateinit var metricNames: Map<String, Int>
        private set

    @PostConstruct
    fun init() {
        val metrics = metricRepository.findAll()
        metricsById = metrics.associateBy { it.id!! }
        metricNames = metrics.associateBy({it.name}, {it.id!!})
        log.info("Initialized metrics: $metricsById")
    }

    fun addNewMetric(metric: Metric): Metric {
        val newMetric = metricRepository.save(metric)
        init()
        return newMetric
    }

    fun getAllMetrics(): List<Metric> {
        return metricsById.values.toList()
    }
}