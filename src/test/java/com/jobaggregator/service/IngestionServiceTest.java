package com.jobaggregator.service;

import com.jobaggregator.adapter.JobSourceAdapter;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import com.jobaggregator.repository.JobRepository;
import com.jobaggregator.service.dedup.DuplicateDetectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngestionServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private DuplicateDetectionService duplicateDetectionService;

    @Test
    void runIngestion_isolatesFailureOfOneAdapterFromOthers() {
        JobSourceAdapter failingAdapter = mock(JobSourceAdapter.class);
        when(failingAdapter.getSource()).thenReturn(JobSource.ADZUNA);
        when(failingAdapter.fetchJobs()).thenThrow(new RuntimeException("boom"));

        JobSourceAdapter workingAdapter = mock(JobSourceAdapter.class);
        when(workingAdapter.getSource()).thenReturn(JobSource.ARBEITNOW);
        NormalizedJob job = newJob();
        when(workingAdapter.fetchJobs()).thenReturn(List.of(job));
        when(jobRepository.findBySourceAndExternalId(any(), anyString())).thenReturn(Optional.empty());
        when(jobRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        IngestionService service = new IngestionService(
                List.of(failingAdapter, workingAdapter), jobRepository, duplicateDetectionService
        );

        service.runIngestion();

        verify(jobRepository, times(1)).save(job);
    }

    private NormalizedJob newJob() {
        Instant now = Instant.now();
        return new NormalizedJob(
                "ext-1",
                JobSource.ARBEITNOW,
                "Backend Developer",
                "Acme GmbH",
                "Berlin",
                null,
                null,
                null,
                "EUR",
                "https://www.arbeitnow.com/view/ext-1",
                now,
                now,
                now
        );
    }
}
