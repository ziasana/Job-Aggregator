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
 *
 * <p>The endpoint moved from {@code pc/v4/jobs} to {@code pc/v6/jobs} at
 * some point after this adapter was first written against the community
 * docs - the old path now silently returns 403 with an empty body for
 * every request, key included. v6 also uses 1-indexed pagination (not
 * 0-indexed) and a materially different response shape ({@code ergebnisliste}
 * instead of {@code stellenangebote}, German field names throughout).
 */
@Service
public class BundesagenturAdapter implements JobSourceAdapter {

    private static final Logger log = LoggerFactory.getLogger(BundesagenturAdapter.class);
    private static final int PAGE_SIZE = 50;
    private static final int FIRST_PAGE = 1;

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
        int page = FIRST_PAGE;
        int pagesFetched = 0;

        while (pagesFetched < properties.maxPages()) {
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

            JsonNode listings = response != null ? response.path("ergebnisliste") : null;
            if (listings == null || !listings.isArray() || listings.isEmpty()) {
                break;
            }

            for (JsonNode listing : listings) {
                NormalizedJob job = toNormalizedJob(listing);
                if (job != null) {
                    jobs.add(job);
                }
            }
            page++;
            pagesFetched++;
        }

        log.info("Bundesagentur: fetched {} jobs across {} page(s)", jobs.size(), pagesFetched);
        return jobs;
    }

    /**
     * Returns {@code null} (and logs at debug) for listings missing a field
     * {@code NormalizedJob} requires (title, external id) - this API isn't
     * officially documented and some listing types (e.g. private-individual
     * postings) don't follow the usual shape. Skipping is preferable to
     * fabricating a value per FR-2.2, and to letting one bad row fail the
     * whole batch upsert.
     */
    private NormalizedJob toNormalizedJob(JsonNode listing) {
        Instant now = Instant.now();
        String externalId = textOrNull(listing, "referenznummer");
        String title = textOrNull(listing, "stellenangebotsTitel");
        if (externalId == null || title == null) {
            log.debug("Bundesagentur: skipping listing missing title/referenznummer: {}", listing);
            return null;
        }

        String location = firstLocation(listing.path("stellenlokationen"));
        String externalUrl = textOrNull(listing, "externeURL");
        String url = externalUrl != null
                ? externalUrl
                : "https://www.arbeitsagentur.de/jobsuche/jobdetail/" + externalId;

        return new NormalizedJob(
                externalId,
                JobSource.BUNDESAGENTUR,
                title,
                textOrNull(listing, "firma"),
                location,
                null,
                null,
                null,
                "EUR",
                url,
                parsePublishedDate(textOrNull(listing, "datumErsteVeroeffentlichung")),
                now,
                now
        );
    }

    private String firstLocation(JsonNode stellenlokationen) {
        if (!stellenlokationen.isArray() || stellenlokationen.isEmpty()) {
            return null;
        }
        return textOrNull(stellenlokationen.get(0).path("adresse"), "ort");
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
