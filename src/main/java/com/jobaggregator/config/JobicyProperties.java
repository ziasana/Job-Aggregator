package com.jobaggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "job-aggregator.jobicy")
public record JobicyProperties(String baseUrl, int count) {
}
