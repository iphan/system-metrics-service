package com.qbio.metrics.metric

import com.fasterxml.jackson.annotation.JsonIgnore
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

    @JsonIgnore
    @Transient
    private lateinit var operand: String

    @JsonIgnore
    @Transient
    lateinit var fields: List<String>

    @PostLoad
    @PostPersist
    @PostUpdate
    fun init() {
        operand =
            if (formula.contains("/")) "/"
            else if (formula.contains("*")) "*"
            else if (formula.contains("-")) "-"
            else "+"
        fields = formula.split(operand).map { it.trim() }
    }

    fun applyOperand(values: List<Double>): Double? {
        return when (operand) {
            "+" -> values.reduceOrNull(Double::plus)
            "-" -> values.reduceOrNull(Double::minus)
            "*" -> values.reduceOrNull(Double::times)
            "/" -> if (values.drop(1).contains(0.0)) null else values.reduceOrNull(Double::div)
            else -> null
        }
    }
}