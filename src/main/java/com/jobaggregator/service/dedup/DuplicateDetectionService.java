package com.jobaggregator.service.dedup;

import com.jobaggregator.config.DedupProperties;
import com.jobaggregator.entity.NormalizedJob;
import com.jobaggregator.repository.JobRepository;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Cross-source duplicate detection (FR-3). Chosen approach: jobs are never
 * merged into a single row - each stays as its own record (so every
 * contributing source is still queryable/attributable), and likely
 * duplicates are flagged by sharing a {@code duplicateGroupId}. Consumers
 * (e.g. search) decide how to present a group.
 *
 * <p>Candidates are blocked by normalized (company, location) to avoid an
 * O(n^2) comparison over the whole table, then compared pairwise within a
 * block using Jaro-Winkler similarity on the normalized title. Only pairs
 * from different sources are considered, since a source isn't expected to
 * duplicate its own listings.
 */
@Service
public class DuplicateDetectionService {

    private static final Logger log = LoggerFactory.getLogger(DuplicateDetectionService.class);

    private final JobRepository jobRepository;
    private final DedupProperties properties;
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    public DuplicateDetectionService(JobRepository jobRepository, DedupProperties properties) {
        this.jobRepository = jobRepository;
        this.properties = properties;
    }

    @Transactional
    public void runDeduplication() {
        List<NormalizedJob> jobs = jobRepository.findAll();

        Map<String, List<NormalizedJob>> blocks = jobs.stream()
                .collect(Collectors.groupingBy(this::blockingKey));

        UnionFind unionFind = new UnionFind();
        jobs.forEach(job -> unionFind.add(job.getId()));

        for (List<NormalizedJob> block : blocks.values()) {
            clusterBlock(block, unionFind);
        }

        Map<UUID, List<NormalizedJob>> clusters = jobs.stream()
                .collect(Collectors.groupingBy(job -> unionFind.find(job.getId())));

        List<NormalizedJob> changed = new ArrayList<>();
        int groupsFound = 0;

        for (List<NormalizedJob> cluster : clusters.values()) {
            if (cluster.size() < 2) {
                if (cluster.get(0).getDuplicateGroupId() != null) {
                    cluster.get(0).setDuplicateGroupId(null);
                    changed.add(cluster.get(0));
                }
                continue;
            }

            groupsFound++;
            UUID groupId = cluster.stream()
                    .map(NormalizedJob::getDuplicateGroupId)
                    .filter(id -> id != null)
                    .min(Comparator.naturalOrder())
                    .orElseGet(UUID::randomUUID);

            for (NormalizedJob job : cluster) {
                if (!groupId.equals(job.getDuplicateGroupId())) {
                    job.setDuplicateGroupId(groupId);
                    changed.add(job);
                }
            }
        }

        if (!changed.isEmpty()) {
            jobRepository.saveAll(changed);
        }

        log.info(
                "Deduplication: {} job(s) scanned, {} duplicate group(s), {} record(s) updated",
                jobs.size(), groupsFound, changed.size()
        );
    }

    private void clusterBlock(List<NormalizedJob> block, UnionFind unionFind) {
        for (int i = 0; i < block.size(); i++) {
            for (int j = i + 1; j < block.size(); j++) {
                NormalizedJob a = block.get(i);
                NormalizedJob b = block.get(j);
                if (a.getSource() == b.getSource()) {
                    continue;
                }
                if (isLikelyDuplicate(a, b)) {
                    unionFind.union(a.getId(), b.getId());
                }
            }
        }
    }

    private boolean isLikelyDuplicate(NormalizedJob a, NormalizedJob b) {
        String titleA = JobTextNormalizer.normalize(a.getTitle());
        String titleB = JobTextNormalizer.normalize(b.getTitle());
        if (titleA.isEmpty() || titleB.isEmpty()) {
            return false;
        }
        return similarity.apply(titleA, titleB) >= properties.titleSimilarityThreshold();
    }

    private String blockingKey(NormalizedJob job) {
        return JobTextNormalizer.normalize(job.getCompany()) + "|" + JobTextNormalizer.normalize(job.getLocation());
    }
}
