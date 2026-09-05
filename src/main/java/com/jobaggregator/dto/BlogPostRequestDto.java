package com.jobaggregator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Admin create/update payload for a blog post. */
public record BlogPostRequestDto(
        @NotBlank String title,
        String category,
        @Size(max = 400) String excerpt,
        @NotBlank String body,
        String coverImageUrl
) {
}
