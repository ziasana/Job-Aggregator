package com.jobaggregator.repository;

import com.jobaggregator.entity.NormalizedJob;
import com.jobaggregator.service.search.CategoryCount;
import com.jobaggregator.service.search.JobSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobSearchRepository {

    Page<NormalizedJob> search(JobSearchCriteria criteria, Pageable pageable);

    List<CategoryCount> topCategories(int limit);
}
