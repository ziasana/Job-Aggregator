package com.jobaggregator.repository;

import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<NormalizedJob, UUID>, JobSearchRepository {

    Optional<NormalizedJob> findBySourceAndExternalId(JobSource source, String externalId);

    List<NormalizedJob> findByDuplicateGroupIdIn(List<UUID> duplicateGroupIds);

    /**
     * Admin listing (FR-admin): every stored row, including hidden ones and
     * cross-source duplicates - unlike {@link JobSearchRepository#search},
     * this deliberately does not collapse duplicate groups, since admins
     * manage individual source rows.
     */
    @Query("""
            SELECT j FROM NormalizedJob j
            WHERE (CAST(:q AS string) IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%'))
                    OR LOWER(j.company) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%')))
              AND (:source IS NULL OR j.source = :source)
              AND (:hidden IS NULL OR j.hidden = :hidden)
            ORDER BY j.lastSeenAt DESC
            """)
    Page<NormalizedJob> searchForAdmin(
            @Param("q") String q,
            @Param("source") JobSource source,
            @Param("hidden") Boolean hidden,
            Pageable pageable
    );
}
