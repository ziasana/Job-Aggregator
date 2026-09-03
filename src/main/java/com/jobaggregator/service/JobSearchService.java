package com.jobaggregator.service;

import com.jobaggregator.dto.JobSummaryDto;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import com.jobaggregator.repository.JobRepository;
import com.jobaggregator.service.search.JobSearchCriteria;
import com.jobaggregator.service.search.JobSortOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobSearchService {

    private final JobRepository jobRepository;

    public JobSearchService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Page<JobSummaryDto> search(JobSearchCriteria rawCriteria, Pageable pageable) {
        JobSearchCriteria criteria = normalize(rawCriteria);
        Page<NormalizedJob> page = jobRepository.search(criteria, pageable);

        Map<UUID, List<JobSource>> sourcesByGroup = loadSiblingSources(page.getContent());
        List<JobSummaryDto> content = page.getContent().stream()
                .map(job -> toDto(job, sourcesByGroup))
                .toList();

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    private JobSearchCriteria normalize(JobSearchCriteria raw) {
        String keyword = blankToNull(raw.keyword());
        JobSortOption sort = raw.sort();
        if (sort == null) {
            sort = keyword != null ? JobSortOption.RELEVANCE : JobSortOption.DATE;
        } else if (sort == JobSortOption.RELEVANCE && keyword == null) {
            sort = JobSortOption.DATE;
        }
        return new JobSearchCriteria(
                keyword, blankToNull(raw.location()), raw.source(), raw.salaryMin(), raw.salaryMax(), sort
        );
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private Map<UUID, List<JobSource>> loadSiblingSources(List<NormalizedJob> jobs) {
        List<UUID> groupIds = jobs.stream()
                .map(NormalizedJob::getDuplicateGroupId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (groupIds.isEmpty()) {
            return Map.of();
        }

        return jobRepository.findByDuplicateGroupIdIn(groupIds).stream()
                .collect(Collectors.groupingBy(
                        NormalizedJob::getDuplicateGroupId,
                        Collectors.mapping(NormalizedJob::getSource, Collectors.toList())
                ));
    }

    private JobSummaryDto toDto(NormalizedJob job, Map<UUID, List<JobSource>> sourcesByGroup) {
        List<JobSource> sources = job.getDuplicateGroupId() != null
                ? sourcesByGroup.getOrDefault(job.getDuplicateGroupId(), List.of(job.getSource()))
                : List.of(job.getSource());

        return new JobSummaryDto(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getLocation(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getCurrency(),
                job.getSource(),
                sources,
                job.getUrl(),
                job.getPublishedAt()
        );
    }
}
