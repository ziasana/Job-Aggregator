package com.jobaggregator.dto;

import com.jobaggregator.entity.JobSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AdminJobDto(
        UUID id,
        String title,
        String company,
        String location,
        String category,
        String summary,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        String currency,
        JobSource source,
        String url,
        Instant publishedAt,
        Instant firstSeenAt,
        Instant lastSeenAt,
        boolean hidden
) {
}
