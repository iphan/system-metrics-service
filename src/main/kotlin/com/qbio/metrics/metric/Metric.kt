package com.qbio.metrics.metric

import javax.persistence.*

@Entity
@Table(name = "metrics")
class Metric(
    var name: String,
    var formula: String
) {
    @Id
    @Column(name = "metricId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null
}