package com.jobaggregator.repository;

import com.jobaggregator.entity.NormalizedJob;
import com.jobaggregator.service.search.CategoryCount;
import com.jobaggregator.service.search.JobSearchCriteria;
import com.jobaggregator.service.search.JobSortOption;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Postgres full-text search (FR-5) over {@code normalized_job}, expressed as
 * a native query so it can use {@code to_tsvector}/{@code ts_rank} directly.
 *
 * <p>Cross-source duplicates (see {@code DuplicateDetectionService}) are
 * collapsed to one representative row per {@code duplicate_group_id} (the
 * most recently seen) before filtering/sorting/paging, so a job posted on
 * two sources shows up once here.
 */
@Repository
public class JobSearchRepositoryImpl implements JobSearchRepository {

    private static final String DEDUPED_CTE = """
            WITH deduped AS (
                SELECT DISTINCT ON (COALESCE(duplicate_group_id, id)) *
                FROM normalized_job
                ORDER BY COALESCE(duplicate_group_id, id), last_seen_at DESC
            )
            """;

    private static final String WHERE_CLAUSE = """
            WHERE j.hidden = false
              AND (CAST(:keyword AS text) IS NULL OR to_tsvector('simple',
                    j.title || ' ' || coalesce(j.company, '') || ' ' || coalesce(j.description, ''))
                    @@ plainto_tsquery('simple', CAST(:keyword AS text)))
              AND (CAST(:location AS text) IS NULL OR j.location ILIKE '%' || CAST(:location AS text) || '%')
              AND (CAST(:source AS text) IS NULL OR j.source = CAST(:source AS text))
              AND (CAST(:category AS text) IS NULL OR j.category = CAST(:category AS text))
              AND (CAST(:salaryMin AS numeric) IS NULL OR j.salary_max >= CAST(:salaryMin AS numeric))
              AND (CAST(:salaryMax AS numeric) IS NULL OR j.salary_min <= CAST(:salaryMax AS numeric))
            """;

    private static final String ORDER_BY_DATE = "ORDER BY j.published_at DESC NULLS LAST, j.id";

    private static final String ORDER_BY_RELEVANCE = """
            ORDER BY ts_rank(to_tsvector('simple',
                    j.title || ' ' || coalesce(j.company, '') || ' ' || coalesce(j.description, '')),
                    plainto_tsquery('simple', CAST(:keyword AS text))) DESC,
                j.published_at DESC NULLS LAST, j.id
            """;

    private final EntityManager entityManager;

    public JobSearchRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<NormalizedJob> search(JobSearchCriteria criteria, Pageable pageable) {
        boolean useRelevance = criteria.sort() == JobSortOption.RELEVANCE && criteria.keyword() != null;
        String orderBy = useRelevance ? ORDER_BY_RELEVANCE : ORDER_BY_DATE;

        String dataSql = DEDUPED_CTE + "SELECT j.* FROM deduped j " + WHERE_CLAUSE + orderBy;
        String countSql = DEDUPED_CTE + "SELECT COUNT(*) FROM deduped j " + WHERE_CLAUSE;

        Query dataQuery = entityManager.createNativeQuery(dataSql, NormalizedJob.class);
        Query countQuery = entityManager.createNativeQuery(countSql);
        bindParameters(dataQuery, criteria);
        bindParameters(countQuery, criteria);

        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<NormalizedJob> content = dataQuery.getResultList();
        long total = ((Number) countQuery.getSingleResult()).longValue();

        return new PageImpl<>(content, pageable, total);
    }

    private void bindParameters(Query query, JobSearchCriteria criteria) {
        query.setParameter("keyword", criteria.keyword());
        query.setParameter("location", criteria.location());
        query.setParameter("source", criteria.source() != null ? criteria.source().name() : null);
        query.setParameter("category", criteria.category());
        query.setParameter("salaryMin", criteria.salaryMin());
        query.setParameter("salaryMax", criteria.salaryMax());
    }

    /** Excludes Adzuna's own "Unknown" fallback category - real data, but not a useful "top category". */
    @Override
    public List<CategoryCount> topCategories(int limit) {
        String sql = DEDUPED_CTE + """
                SELECT j.category, COUNT(*) AS job_count
                FROM deduped j
                WHERE j.hidden = false
                  AND j.category IS NOT NULL AND j.category NOT ILIKE 'unknown'
                GROUP BY j.category
                ORDER BY job_count DESC
                LIMIT :limit
                """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        return rows.stream()
                .map(row -> new CategoryCount((String) row[0], ((Number) row[1]).longValue()))
                .toList();
    }
}
