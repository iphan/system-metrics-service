package com.qbio.metrics.measurement

import java.time.LocalDateTime

data class MeasurementDTO(val metricName: String, val values: List<TimedValue>)

data class TimedValue(val timestamp: LocalDateTime, val value: Double) {
    constructor(measurement: Measurement) : this(measurement.timestamp, measurement.value)
}

data class AggregatedMeasurementDTO(val metricName: String, val value: Double)