package com.jobaggregator.service.dedup;

import com.jobaggregator.config.DedupProperties;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import com.jobaggregator.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DuplicateDetectionServiceTest {

    @Mock
    private JobRepository jobRepository;

    private final DedupProperties properties = new DedupProperties(0.90);

    @Test
    void runDeduplication_groupsSimilarJobsAcrossSources() {
        NormalizedJob arbeitnowJob = newJob(JobSource.ARBEITNOW, "Backend Developer", "Acme GmbH", "Berlin");
        NormalizedJob adzunaJob = newJob(JobSource.ADZUNA, "Backend Developer (m/f/d)", "Acme GmbH", "Berlin");
        NormalizedJob unrelatedJob = newJob(JobSource.ARBEITNOW, "Marketing Manager", "Other Corp", "Munich");

        when(jobRepository.findAll()).thenReturn(List.of(arbeitnowJob, adzunaJob, unrelatedJob));

        DuplicateDetectionService service = new DuplicateDetectionService(jobRepository, properties);
        service.runDeduplication();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<NormalizedJob>> captor = ArgumentCaptor.forClass(List.class);
        verify(jobRepository).saveAll(captor.capture());

        List<NormalizedJob> saved = captor.getValue();
        assertThat(saved).containsExactlyInAnyOrder(arbeitnowJob, adzunaJob);
        assertThat(arbeitnowJob.getDuplicateGroupId()).isNotNull();
        assertThat(arbeitnowJob.getDuplicateGroupId()).isEqualTo(adzunaJob.getDuplicateGroupId());
        assertThat(unrelatedJob.getDuplicateGroupId()).isNull();
    }

    @Test
    void runDeduplication_doesNotGroupSameSourceListings() {
        NormalizedJob first = newJob(JobSource.ARBEITNOW, "Backend Developer", "Acme GmbH", "Berlin");
        NormalizedJob second = newJob(JobSource.ARBEITNOW, "Backend Developer", "Acme GmbH", "Berlin");

        when(jobRepository.findAll()).thenReturn(List.of(first, second));

        DuplicateDetectionService service = new DuplicateDetectionService(jobRepository, properties);
        service.runDeduplication();

        assertThat(first.getDuplicateGroupId()).isNull();
        assertThat(second.getDuplicateGroupId()).isNull();
    }

    private NormalizedJob newJob(JobSource source, String title, String company, String location) {
        Instant now = Instant.now();
        NormalizedJob job = new NormalizedJob(
                UUID.randomUUID().toString(),
                source,
                title,
                company,
                location,
                null,
                null,
                null,
                "EUR",
                "https://example.com/" + UUID.randomUUID(),
                now,
                now,
                now
        );
        setId(job);
        return job;
    }

    private void setId(NormalizedJob job) {
        try {
            var field = NormalizedJob.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(job, UUID.randomUUID());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
