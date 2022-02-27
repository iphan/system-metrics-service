package com.qbio.metrics.measurement

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "measurements")
class Measurement(
    var metricId: Int,
    var timestamp: LocalDateTime,
    var value: Double
) {
    @Id
    @Column(name = "measurementId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}