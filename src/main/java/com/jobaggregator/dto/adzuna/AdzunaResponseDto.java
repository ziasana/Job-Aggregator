package com.jobaggregator.dto.adzuna;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AdzunaResponseDto(List<AdzunaJobDto> results, Integer count) {
}
