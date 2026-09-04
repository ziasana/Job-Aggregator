package com.jobaggregator.adapter;

import com.jobaggregator.config.JobicyProperties;
import com.jobaggregator.dto.jobicy.JobicyJobDto;
import com.jobaggregator.dto.jobicy.JobicyResponseDto;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Jobicy remote-jobs API adapter (https://jobicy.com/api/v2/remote-jobs).
 *
 * <p>Free, public, no API key. Unlike the other sources, this returns
 * global remote listings (not Germany-specific) and isn't paginated -
 * {@code count} caps at 200 per Jobicy's own limit, so one request per
 * ingestion run is all there is. Jobicy's own usage notice asks that
 * results link directly back to the job's original URL, which is exactly
 * how every source is already displayed here.
 */
@Service
public class JobicyAdapter implements JobSourceAdapter {

    private static final Logger log = LoggerFactory.getLogger(JobicyAdapter.class);

    private final RestClient restClient;
    private final JobicyProperties properties;

    public JobicyAdapter(RestClient.Builder restClientBuilder, JobicyProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    @Override
    public JobSource getSource() {
        return JobSource.JOBICY;
    }

    @Override
    public List<NormalizedJob> fetchJobs() {
        String url = UriComponentsBuilder
                .fromUriString(properties.baseUrl())
                .queryParam("count", properties.count())
                .toUriString();

        JobicyResponseDto response = restClient.get().uri(url).retrieve().body(JobicyResponseDto.class);
        List<JobicyJobDto> listings = response != null && response.jobs() != null ? response.jobs() : List.of();

        List<NormalizedJob> jobs = listings.stream().map(this::toNormalizedJob).toList();
        log.info("Jobicy: fetched {} jobs", jobs.size());
        return jobs;
    }

    private NormalizedJob toNormalizedJob(JobicyJobDto dto) {
        Instant now = Instant.now();

        return new NormalizedJob(
                Long.toString(dto.id()),
                JobSource.JOBICY,
                dto.jobTitle(),
                dto.companyName(),
                dto.jobGeo(),
                dto.jobExcerpt(),
                dto.salaryMin(),
                dto.salaryMax(),
                dto.salaryCurrency(),
                dto.url(),
                parsePublishedDate(dto.pubDate()),
                now,
                now
        );
    }

    private Instant parsePublishedDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value).toInstant();
        } catch (DateTimeParseException e) {
            log.debug("Jobicy: could not parse published date '{}'", value);
            return null;
        }
    }
}
