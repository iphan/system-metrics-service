package com.qbio.metrics.collector

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
class CollectorController {

    @Autowired
    private lateinit var collectorService: CollectorService

    @Autowired
    private lateinit var collectorParser: CollectorParser

    @PutMapping("collector/on")
    fun turnOnCollector(): ResponseEntity<Void> {
        collectorService.startCollection()
        return ResponseEntity.ok().build()
    }

    @PutMapping("collector/off")
    fun turnOffCollector(): ResponseEntity<Void> {
        collectorService.stopCollection()
        return ResponseEntity.ok().build()
    }

    @GetMapping("collector/headers")
    fun gerHeaders(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(collectorParser.headers)
    }
}