package com.jobaggregator.repository;

import com.jobaggregator.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BlogPostRepository extends JpaRepository<BlogPost, UUID> {

    Optional<BlogPost> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<BlogPost> findAllByOrderByPublishedAtDesc(Pageable pageable);
}
