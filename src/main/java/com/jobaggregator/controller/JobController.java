package com.jobaggregator.controller;

import com.jobaggregator.dto.JobSummaryDto;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.service.JobSearchService;
import com.jobaggregator.service.search.JobSearchCriteria;
import com.jobaggregator.service.search.JobSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Search/filter/list API (FR-5, FR-6). Filter params are parsed leniently -
 * an unparseable or unknown value is ignored rather than rejected with a
 * 400 (FR-6.3) - since a bad filter is still a valid "show me everything
 * else" request.
 *
 * <p>{@code sortBy} is this API's own relevance-vs-date switch; it's
 * deliberately not named {@code sort} to avoid colliding with Spring Data's
 * page/size/sort binding on {@link Pageable} (whose {@code Sort} isn't used
 * here - ranking comes from Postgres full-text search, not a column order).
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);

    private final JobSearchService jobSearchService;

    public JobController(JobSearchService jobSearchService) {
        this.jobSearchService = jobSearchService;
    }

    @GetMapping
    public Page<JobSummaryDto> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String salaryMin,
            @RequestParam(required = false) String salaryMax,
            @RequestParam(required = false) String sortBy,
            Pageable pageable
    ) {
        JobSearchCriteria criteria = new JobSearchCriteria(
                q, location, parseSource(source), parseDecimal(salaryMin, "salaryMin"),
                parseDecimal(salaryMax, "salaryMax"), parseSort(sortBy)
        );
        return jobSearchService.search(criteria, pageable);
    }

    private JobSource parseSource(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return JobSource.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.debug("Ignoring unknown source filter '{}'", raw);
            return null;
        }
    }

    private BigDecimal parseDecimal(String raw, String paramName) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException e) {
            log.debug("Ignoring unparseable {} filter '{}'", paramName, raw);
            return null;
        }
    }

    private JobSortOption parseSort(String raw) {
        if (raw == null) {
            return null;
        }
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "relevance" -> JobSortOption.RELEVANCE;
            case "date" -> JobSortOption.DATE;
            default -> null;
        };
    }
}
