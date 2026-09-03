package com.jobaggregator.service.search;

import com.jobaggregator.entity.JobSource;

import java.math.BigDecimal;

public record JobSearchCriteria(
        String keyword,
        String location,
        JobSource source,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        JobSortOption sort
) {
}
