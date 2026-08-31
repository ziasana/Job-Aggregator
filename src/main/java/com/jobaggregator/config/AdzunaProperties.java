package com.jobaggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "job-aggregator.adzuna")
public record AdzunaProperties(
        String baseUrl,
        String country,
        String appId,
        String appKey,
        int maxPages
) {
    public boolean hasCredentials() {
        return appId != null && !appId.isBlank() && appKey != null && !appKey.isBlank();
    }
}
