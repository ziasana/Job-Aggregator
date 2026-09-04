package com.jobaggregator.dto.jobicy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JobicyResponseDto(List<JobicyJobDto> jobs) {
}
