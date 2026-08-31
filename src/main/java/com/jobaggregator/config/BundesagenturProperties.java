package com.jobaggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "job-aggregator.bundesagentur")
public record BundesagenturProperties(String baseUrl, String apiKey, int maxPages) {

    public boolean hasCredentials() {
        return apiKey != null && !apiKey.isBlank();
    }
}
