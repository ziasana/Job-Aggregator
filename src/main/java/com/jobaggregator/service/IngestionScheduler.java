package com.jobaggregator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Periodic ingestion trigger (FR-1.1). Disabled in tests via the
 * "test" profile's application-test.yml, so {@code @SpringBootTest} runs
 * don't fire real HTTP calls to every external API on context startup.
 */
@Component
@ConditionalOnProperty(
        prefix = "job-aggregator.ingestion",
        name = "scheduling-enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class IngestionScheduler {

    private static final Logger log = LoggerFactory.getLogger(IngestionScheduler.class);

    private final IngestionService ingestionService;

    public IngestionScheduler(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Scheduled(fixedRateString = "${job-aggregator.ingestion.interval:PT6H}")
    public void runScheduledIngestion() {
        log.info("Scheduled ingestion starting");
        Instant start = Instant.now();
        ingestionService.runIngestion();
        log.info("Scheduled ingestion finished in {}", Duration.between(start, Instant.now()));
    }
}
