package com.jobaggregator.repository;

import com.jobaggregator.entity.NormalizedJob;
import com.jobaggregator.service.search.JobSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobSearchRepository {

    Page<NormalizedJob> search(JobSearchCriteria criteria, Pageable pageable);
}
