package com.qbio.metrics.metric

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class MetricService {

    @Autowired
    private lateinit var metricRepository: MetricRepository

    final lateinit var metricFormulas: Map<Int, String>
        private set

    final lateinit var metricNames: Map<String, Int>
        private set

    @PostConstruct
    fun init() {
        val metrics = metricRepository.findAll()
        metricFormulas = metrics.associateBy({it.id!!}, {it.formula})
        metricNames = metrics.associateBy({it.name}, {it.id!!})
    }

    fun addNewMetric(metric: Metric): Metric {
        val newMetric = metricRepository.save(metric)
        init()
        return newMetric
    }
}