package com.jobaggregator.adapter;

import com.jobaggregator.config.AdzunaProperties;
import com.jobaggregator.dto.adzuna.AdzunaJobDto;
import com.jobaggregator.dto.adzuna.AdzunaResponseDto;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdzunaAdapter implements JobSourceAdapter {

    private static final Logger log = LoggerFactory.getLogger(AdzunaAdapter.class);
    private static final int RESULTS_PER_PAGE = 50;

    private final RestClient restClient;
    private final AdzunaProperties properties;

    public AdzunaAdapter(RestClient.Builder restClientBuilder, AdzunaProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    @Override
    public JobSource getSource() {
        return JobSource.ADZUNA;
    }

    @Override
    public List<NormalizedJob> fetchJobs() {
        if (!properties.hasCredentials()) {
            log.warn("Adzuna: skipped, missing ADZUNA_APP_ID/ADZUNA_APP_KEY credentials");
            return List.of();
        }

        List<NormalizedJob> jobs = new ArrayList<>();
        int page = 1;
        int totalCount = Integer.MAX_VALUE;

        while (page <= properties.maxPages() && jobs.size() < totalCount) {
            String url = UriComponentsBuilder
                    .fromUriString(properties.baseUrl() + "/" + properties.country() + "/search/" + page)
                    .queryParam("app_id", properties.appId())
                    .queryParam("app_key", properties.appKey())
                    .queryParam("results_per_page", RESULTS_PER_PAGE)
                    .queryParam("content-type", "application/json")
                    .toUriString();

            AdzunaResponseDto response = restClient.get().uri(url).retrieve().body(AdzunaResponseDto.class);

            if (response == null || response.results() == null || response.results().isEmpty()) {
                break;
            }

            totalCount = response.count() != null ? response.count() : jobs.size() + response.results().size();
            for (AdzunaJobDto dto : response.results()) {
                jobs.add(toNormalizedJob(dto));
            }
            page++;
        }

        log.info("Adzuna: fetched {} jobs across {} page(s)", jobs.size(), page - 1);
        return jobs;
    }

    private NormalizedJob toNormalizedJob(AdzunaJobDto dto) {
        Instant now = Instant.now();
        Instant publishedAt = parseCreated(dto.created());
        String company = dto.company() != null ? dto.company().displayName() : null;
        String location = dto.location() != null ? dto.location().displayName() : null;

        return new NormalizedJob(
                dto.id(),
                JobSource.ADZUNA,
                dto.title(),
                company,
                location,
                dto.salaryMin(),
                dto.salaryMax(),
                "EUR",
                dto.redirectUrl(),
                publishedAt,
                now,
                now
        );
    }

    private Instant parseCreated(String created) {
        if (created == null || created.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(created);
        } catch (DateTimeParseException e) {
            log.debug("Adzuna: could not parse published date '{}'", created);
            return null;
        }
    }
}
