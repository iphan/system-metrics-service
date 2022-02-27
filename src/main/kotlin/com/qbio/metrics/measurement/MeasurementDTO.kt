package com.qbio.metrics.measurement

import java.time.LocalDateTime

data class DataSetDTO(val metricName: String, val values: List<DataPoint>)

data class DataPoint(val timestamp: LocalDateTime, val value: Double) {
    constructor(measurement: Measurement) : this(measurement.timestamp, measurement.value)
}

data class MeasurementDTO(val metricName: String, val value: Double)