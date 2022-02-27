package com.qbio.metrics.measurement

import java.util.Date
import javax.persistence.*

@Entity
@Table(name = "measurements")
class Measurement(
    @Id
    @Column(name = "measurementId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var metricId: Int,
    var timestamp: Date,
    var value: Double
) {
}