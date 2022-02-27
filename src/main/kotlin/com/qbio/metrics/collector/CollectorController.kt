package com.qbio.metrics.collector

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
class CollectorController {

    @Autowired
    private lateinit var collectorService: CollectorService

    @PostMapping("collector/on")
    fun turnOnCollector(): ResponseEntity<Void> {
        collectorService.startCollection()
        return ResponseEntity.ok().build()
    }

    @PostMapping("collector/off")
    fun turnOffCollector(): ResponseEntity<Void> {
        collectorService.stopCollection()
        return ResponseEntity.ok().build()
    }
}