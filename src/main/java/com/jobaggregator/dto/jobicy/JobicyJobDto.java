package com.jobaggregator.dto.jobicy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JobicyJobDto(
        long id,
        String url,
        String jobTitle,
        String companyName,
        String jobGeo,
        String jobExcerpt,
        String pubDate,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        String salaryCurrency
) {
}
