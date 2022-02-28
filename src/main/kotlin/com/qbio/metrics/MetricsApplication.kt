package com.qbio.metrics

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class MetricsApplication

fun main(args: Array<String>) {
	runApplication<MetricsApplication>(*args)
}
