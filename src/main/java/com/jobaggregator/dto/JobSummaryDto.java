package com.jobaggregator.dto;

import com.jobaggregator.entity.JobSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * API-facing shape for one search result (FR-6.2): enough to render a
 * result card and link back to the original listing. {@code sources}
 * lists every source contributing to this job when it was flagged as a
 * cross-source duplicate (FR-3.2); otherwise it's just {@code source}.
 */
public record JobSummaryDto(
        UUID id,
        String title,
        String company,
        String location,
        String category,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        String currency,
        JobSource source,
        List<JobSource> sources,
        String url,
        Instant publishedAt
) {
}
