package com.jobaggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "job-aggregator.dedup")
public record DedupProperties(double titleSimilarityThreshold) {
}
