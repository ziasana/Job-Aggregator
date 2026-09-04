package com.jobaggregator.dto;

/** One entry of the "top categories" breakdown (real counts, no fabricated data). */
public record CategorySummaryDto(String category, long count) {
}
