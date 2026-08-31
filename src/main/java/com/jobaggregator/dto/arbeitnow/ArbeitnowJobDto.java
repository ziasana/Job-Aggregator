package com.jobaggregator.dto.arbeitnow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ArbeitnowJobDto(
        String slug,
        String companyName,
        String title,
        String description,
        String url,
        String location,
        Long createdAt
) {
}
