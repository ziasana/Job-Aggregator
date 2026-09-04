package com.jobaggregator.repository;

import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import com.jobaggregator.service.search.JobSearchCriteria;
import com.jobaggregator.service.search.JobSortOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JobSearchRepositoryImplTest {

    @Autowired
    private JobRepository jobRepository;

    @BeforeEach
    void seed() {
        jobRepository.deleteAll();
        jobRepository.save(job("1", JobSource.ARBEITNOW, "Backend Developer", "Acme GmbH", "Berlin",
                "Work on our Spring Boot backend.", new BigDecimal("50000"), new BigDecimal("60000"), 2));
        jobRepository.save(job("2", JobSource.ADZUNA, "Frontend Developer", "Beta AG", "Hamburg",
                "React and TypeScript role.", new BigDecimal("55000"), new BigDecimal("65000"), 1));
        jobRepository.save(job("3", JobSource.ARBEITNOW, "Data Analyst", "Gamma KG", "Munich",
                "SQL and reporting.", null, null, 0));
    }

    @Test
    void search_filtersByKeywordAcrossTitleCompanyAndDescription() {
        Page<NormalizedJob> page = jobRepository.search(
                new JobSearchCriteria("Spring Boot", null, null, null, null, JobSortOption.RELEVANCE),
                PageRequest.of(0, 10)
        );

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getExternalId()).isEqualTo("1");
    }

    @Test
    void search_filtersByLocationAndSource() {
        Page<NormalizedJob> page = jobRepository.search(
                new JobSearchCriteria(null, "Hamburg", JobSource.ADZUNA, null, null, JobSortOption.DATE),
                PageRequest.of(0, 10)
        );

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getExternalId()).isEqualTo("2");
    }

    @Test
    void search_filtersBySalaryRangeAndExcludesJobsWithoutSalary() {
        Page<NormalizedJob> page = jobRepository.search(
                new JobSearchCriteria(null, null, null, new BigDecimal("52000"), new BigDecimal("70000"), JobSortOption.DATE),
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent())
                .extracting(NormalizedJob::getExternalId)
                .containsExactlyInAnyOrder("1", "2");
    }

    @Test
    void search_withNoFilters_sortsByDateDescendingAndPaginates() {
        Page<NormalizedJob> page = jobRepository.search(
                new JobSearchCriteria(null, null, null, null, null, JobSortOption.DATE),
                PageRequest.of(0, 10)
        );

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent())
                .extracting(NormalizedJob::getExternalId)
                .containsExactly("3", "2", "1");
    }

    private NormalizedJob job(
            String externalId, JobSource source, String title, String company, String location,
            String description, BigDecimal salaryMin, BigDecimal salaryMax, long daysAgo
    ) {
        Instant publishedAt = Instant.now().minus(daysAgo, ChronoUnit.DAYS);
        return new NormalizedJob(
                externalId, source, title, company, location, description, salaryMin, salaryMax,
                "EUR", "https://example.com/" + externalId, publishedAt, Instant.now(), Instant.now()
        );
    }
}
