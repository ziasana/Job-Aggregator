package com.jobaggregator.dto.adzuna;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AdzunaJobDto(
        String id,
        String title,
        String description,
        AdzunaCompanyDto company,
        AdzunaLocationDto location,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        String redirectUrl,
        String created
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record AdzunaCompanyDto(String displayName) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record AdzunaLocationDto(String displayName) {
    }
}
