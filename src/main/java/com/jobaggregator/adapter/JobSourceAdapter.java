package com.jobaggregator.adapter;

import com.jobaggregator.entity.JobSource;
import com.jobaggregator.entity.NormalizedJob;

import java.util.List;

public interface JobSourceAdapter {

    JobSource getSource();

    List<NormalizedJob> fetchJobs();
}
