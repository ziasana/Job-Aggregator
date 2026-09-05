package com.jobaggregator.service;

import com.jobaggregator.dto.AdminJobDto;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import com.jobaggregator.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AdminJobService {

    private final JobRepository jobRepository;

    public AdminJobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Page<AdminJobDto> search(String q, JobSource source, Boolean hidden, Pageable pageable) {
        String keyword = (q == null || q.isBlank()) ? null : q.trim();
        return jobRepository.searchForAdmin(keyword, source, hidden, pageable).map(this::toDto);
    }

    public AdminJobDto getById(UUID id) {
        return jobRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Job not found: " + id));
    }

    @Transactional
    public AdminJobDto setHidden(UUID id, boolean hidden) {
        NormalizedJob job = jobRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Job not found: " + id));
        job.setHidden(hidden);
        return toDto(job);
    }

    @Transactional
    public void delete(UUID id) {
        if (!jobRepository.existsById(id)) {
            throw new NoSuchElementException("Job not found: " + id);
        }
        jobRepository.deleteById(id);
    }

    private AdminJobDto toDto(NormalizedJob job) {
        return new AdminJobDto(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getLocation(),
                job.getCategory(),
                job.getSummary(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getCurrency(),
                job.getSource(),
                job.getUrl(),
                job.getPublishedAt(),
                job.getFirstSeenAt(),
                job.getLastSeenAt(),
                job.isHidden()
        );
    }
}
