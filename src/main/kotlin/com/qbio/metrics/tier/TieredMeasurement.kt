package com.qbio.metrics.tier

import com.qbio.metrics.measurement.Measurement
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "tiered_measurements")
class TieredMeasurement(
    var tier: Tier,
    var metricId: Int,
    var timestamp: LocalDateTime,
    var sum: Double,
    var max: Double,
    var min: Double,
    var sampleSize: Long
) {
    @Id
    @Column(name = "tiered_measurement_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    constructor(tier: Tier, metricId: Int, timestamp: LocalDateTime, values: List<Double>) :
            this(tier, metricId, timestamp,
                values.reduce(Double::plus),
                values.reduce(Math::max),
                values.reduce(Math::min),
                values.count().toLong())
}