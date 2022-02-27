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

    @PostConstruct
    fun init() {
        metricFormulas = metricRepository.findAll()
            .associateBy({it.id!!}, {it.formula})
    }
}