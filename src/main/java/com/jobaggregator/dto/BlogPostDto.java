package com.jobaggregator.dto;

import java.time.Instant;
import java.util.UUID;

/** Full blog post (detail view and admin edit form). */
public record BlogPostDto(
        UUID id,
        String title,
        String slug,
        String category,
        String excerpt,
        String body,
        String coverImageUrl,
        Instant publishedAt,
        Instant updatedAt
) {
}
