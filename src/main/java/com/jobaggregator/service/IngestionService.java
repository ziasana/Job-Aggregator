package com.jobaggregator.service;

import com.jobaggregator.adapter.JobSourceAdapter;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import com.jobaggregator.repository.JobRepository;
import com.jobaggregator.service.dedup.DuplicateDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class IngestionService {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);

    private final List<JobSourceAdapter> adapters;
    private final JobRepository jobRepository;
    private final DuplicateDetectionService duplicateDetectionService;

    public IngestionService(
            List<JobSourceAdapter> adapters,
            JobRepository jobRepository,
            DuplicateDetectionService duplicateDetectionService
    ) {
        this.adapters = adapters;
        this.jobRepository = jobRepository;
        this.duplicateDetectionService = duplicateDetectionService;
    }

    public void runIngestion() {
        for (JobSourceAdapter adapter : adapters) {
            runForSource(adapter);
        }
        duplicateDetectionService.runDeduplication();
    }

    private void runForSource(JobSourceAdapter adapter) {
        JobSource source = adapter.getSource();
        try {
            List<NormalizedJob> fetched = adapter.fetchJobs();
            IngestionResult result = upsertAll(fetched);
            log.info(
                    "Ingestion[{}]: fetched={}, new={}, updated={}",
                    source, fetched.size(), result.created(), result.updated()
            );
        } catch (Exception e) {
            log.error("Ingestion[{}]: failed - {}", source, e.getMessage(), e);
        }
    }

    @Transactional
    IngestionResult upsertAll(List<NormalizedJob> jobs) {
        int created = 0;
        int updated = 0;
        for (NormalizedJob job : jobs) {
            if (upsert(job)) {
                created++;
            } else {
                updated++;
            }
        }
        return new IngestionResult(created, updated);
    }

    private boolean upsert(NormalizedJob incoming) {
        Optional<NormalizedJob> existing = jobRepository.findBySourceAndExternalId(
                incoming.getSource(), incoming.getExternalId()
        );

        if (existing.isPresent()) {
            NormalizedJob job = existing.get();
            job.setTitle(incoming.getTitle());
            job.setCompany(incoming.getCompany());
            job.setLocation(incoming.getLocation());
            job.setDescription(incoming.getDescription());
            job.setCategory(incoming.getCategory());
            job.setSalaryMin(incoming.getSalaryMin());
            job.setSalaryMax(incoming.getSalaryMax());
            job.setCurrency(incoming.getCurrency());
            job.setUrl(incoming.getUrl());
            job.setPublishedAt(incoming.getPublishedAt());
            job.setLastSeenAt(Instant.now());
            jobRepository.save(job);
            return false;
        }

        jobRepository.save(incoming);
        return true;
    }

    private record IngestionResult(int created, int updated) {
    }
}
