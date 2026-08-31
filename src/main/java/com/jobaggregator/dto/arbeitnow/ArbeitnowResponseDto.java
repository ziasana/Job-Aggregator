package com.jobaggregator.dto.arbeitnow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ArbeitnowResponseDto(List<ArbeitnowJobDto> data, ArbeitnowLinksDto links) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ArbeitnowLinksDto(String next) {
    }
}
