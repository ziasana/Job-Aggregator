package com.jobaggregator.adapter;

import com.jobaggregator.config.ArbeitnowProperties;
import com.jobaggregator.dto.arbeitnow.ArbeitnowJobDto;
import com.jobaggregator.dto.arbeitnow.ArbeitnowResponseDto;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArbeitnowAdapter implements JobSourceAdapter {

    private static final Logger log = LoggerFactory.getLogger(ArbeitnowAdapter.class);
    private static final long PAGE_DELAY_MILLIS = 300;

    private final RestClient restClient;
    private final ArbeitnowProperties properties;

    public ArbeitnowAdapter(RestClient.Builder restClientBuilder, ArbeitnowProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    @Override
    public JobSource getSource() {
        return JobSource.ARBEITNOW;
    }

    @Override
    public List<NormalizedJob> fetchJobs() {
        List<NormalizedJob> jobs = new ArrayList<>();
        String currentUrl = properties.baseUrl();
        int page = 0;

        while (page < properties.maxPages()) {
            if (page > 0) {
                sleepBetweenPages();
            }

            ArbeitnowResponseDto response = restClient.get()
                    .uri(currentUrl)
                    .retrieve()
                    .body(ArbeitnowResponseDto.class);

            if (response == null || response.data() == null) {
                break;
            }

            for (ArbeitnowJobDto dto : response.data()) {
                jobs.add(toNormalizedJob(dto));
            }

            page++;
            String next = response.links() != null ? response.links().next() : null;
            if (next == null || next.isBlank()) {
                break;
            }
            currentUrl = next;
        }

        log.info("Arbeitnow: fetched {} jobs across {} page(s)", jobs.size(), page);
        return jobs;
    }

    private void sleepBetweenPages() {
        try {
            Thread.sleep(PAGE_DELAY_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private NormalizedJob toNormalizedJob(ArbeitnowJobDto dto) {
        Instant now = Instant.now();
        Instant publishedAt = dto.createdAt() != null ? Instant.ofEpochSecond(dto.createdAt()) : null;

        return new NormalizedJob(
                dto.slug(),
                JobSource.ARBEITNOW,
                dto.title(),
                dto.companyName(),
                dto.location(),
                dto.description(),
                null,
                null,
                "EUR",
                dto.url(),
                publishedAt,
                now,
                now
        );
    }
}
