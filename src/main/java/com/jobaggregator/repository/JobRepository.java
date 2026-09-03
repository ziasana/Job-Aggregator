package com.jobaggregator.repository;

import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<NormalizedJob, UUID>, JobSearchRepository {

    Optional<NormalizedJob> findBySourceAndExternalId(JobSource source, String externalId);

    List<NormalizedJob> findByDuplicateGroupIdIn(List<UUID> duplicateGroupIds);
}
