package com.jobaggregator.dto;

import java.time.Instant;
import java.util.UUID;

/** Blog list view (public and admin) - no body, so listing pages stay light. */
public record BlogPostSummaryDto(
        UUID id,
        String title,
        String slug,
        String category,
        String excerpt,
        String coverImageUrl,
        Instant publishedAt
) {
}
