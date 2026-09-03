package com.jobaggregator.adapter;

import tools.jackson.databind.JsonNode;
import com.jobaggregator.config.BundesagenturProperties;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Bundesagentur fuer Arbeit Jobsuche API adapter.
 *
 * <p>This uses a community-documented endpoint and shared API key
 * ({@code X-API-Key: jobboerse-jobsuche}), not an officially sanctioned
 * third-party integration. See README for details.
 */
@Service
public class BundesagenturAdapter implements JobSourceAdapter {

    private static final Logger log = LoggerFactory.getLogger(BundesagenturAdapter.class);
    private static final int PAGE_SIZE = 50;

    private final RestClient restClient;
    private final BundesagenturProperties properties;

    public BundesagenturAdapter(RestClient.Builder restClientBuilder, BundesagenturProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    @Override
    public JobSource getSource() {
        return JobSource.BUNDESAGENTUR;
    }

    @Override
    public List<NormalizedJob> fetchJobs() {
        if (!properties.hasCredentials()) {
            log.warn("Bundesagentur: skipped, missing BUNDESAGENTUR_API_KEY");
            return List.of();
        }

        List<NormalizedJob> jobs = new ArrayList<>();
        int page = 0;

        while (page < properties.maxPages()) {
            String url = UriComponentsBuilder
                    .fromUriString(properties.baseUrl())
                    .queryParam("page", page)
                    .queryParam("size", PAGE_SIZE)
                    .toUriString();

            JsonNode response = restClient.get()
                    .uri(url)
                    .header("X-API-Key", properties.apiKey())
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode listings = response != null ? response.path("stellenangebote") : null;
            if (listings == null || !listings.isArray() || listings.isEmpty()) {
                break;
            }

            for (JsonNode listing : listings) {
                jobs.add(toNormalizedJob(listing));
            }
            page++;
        }

        log.info("Bundesagentur: fetched {} jobs across {} page(s)", jobs.size(), page);
        return jobs;
    }

    private NormalizedJob toNormalizedJob(JsonNode listing) {
        Instant now = Instant.now();
        String externalId = textOrNull(listing, "refnr");
        String location = textOrNull(listing.path("arbeitsort"), "ort");
        String detailUrl = externalId != null
                ? "https://www.arbeitsagentur.de/jobsuche/jobdetail/" + externalId
                : null;

        return new NormalizedJob(
                externalId,
                JobSource.BUNDESAGENTUR,
                textOrNull(listing, "titel"),
                textOrNull(listing, "arbeitgeber"),
                location,
                null,
                null,
                null,
                "EUR",
                detailUrl,
                parsePublishedDate(textOrNull(listing, "aktuelleVeroeffentlichungsdatum")),
                now,
                now
        );
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private Instant parsePublishedDate(String isoDate) {
        if (isoDate == null || isoDate.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(isoDate).atStartOfDay(ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException e) {
            log.debug("Bundesagentur: could not parse published date '{}'", isoDate);
            return null;
        }
    }
}
