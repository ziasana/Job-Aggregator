package com.jobaggregator.service;

import com.jobaggregator.dto.BlogPostDto;
import com.jobaggregator.dto.BlogPostRequestDto;
import com.jobaggregator.dto.BlogPostSummaryDto;
import com.jobaggregator.entity.BlogPost;
import com.jobaggregator.repository.BlogPostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class BlogPostService {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");

    private final BlogPostRepository blogPostRepository;

    public BlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    public Page<BlogPostSummaryDto> list(Pageable pageable) {
        return blogPostRepository.findAllByOrderByPublishedAtDesc(pageable).map(this::toSummaryDto);
    }

    public BlogPostDto getBySlug(String slug) {
        return blogPostRepository.findBySlug(slug)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Blog post not found: " + slug));
    }

    public BlogPostDto getById(UUID id) {
        return blogPostRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Blog post not found: " + id));
    }

    @Transactional
    public BlogPostDto create(BlogPostRequestDto request) {
        Instant now = Instant.now();
        BlogPost post = new BlogPost(
                request.title().trim(),
                uniqueSlug(slugify(request.title())),
                blankToNull(request.category()),
                excerptOrFallback(request.excerpt(), request.body()),
                request.body().trim(),
                blankToNull(request.coverImageUrl()),
                now,
                now
        );
        return toDto(blogPostRepository.save(post));
    }

    @Transactional
    public BlogPostDto update(UUID id, BlogPostRequestDto request) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Blog post not found: " + id));
        post.setTitle(request.title().trim());
        post.setCategory(blankToNull(request.category()));
        post.setExcerpt(excerptOrFallback(request.excerpt(), request.body()));
        post.setBody(request.body().trim());
        post.setCoverImageUrl(blankToNull(request.coverImageUrl()));
        post.setUpdatedAt(Instant.now());
        return toDto(post);
    }

    @Transactional
    public void delete(UUID id) {
        if (!blogPostRepository.existsById(id)) {
            throw new NoSuchElementException("Blog post not found: " + id);
        }
        blogPostRepository.deleteById(id);
    }

    /** Slugs are generated once at creation and never change, so published URLs stay stable. */
    private String uniqueSlug(String base) {
        String candidate = base.isBlank() ? "post" : base;
        String slug = candidate;
        int suffix = 2;
        while (blogPostRepository.existsBySlug(slug)) {
            slug = candidate + "-" + suffix++;
        }
        return slug;
    }

    private String slugify(String title) {
        String lower = title.trim().toLowerCase(Locale.ROOT);
        String hyphenated = NON_ALPHANUMERIC.matcher(lower).replaceAll("-");
        return hyphenated.replaceAll("^-+|-+$", "");
    }

    /** Body is HTML (rich-text editor output) - strip tags/entities for a plain-text fallback excerpt. */
    private String excerptOrFallback(String excerpt, String body) {
        if (excerpt != null && !excerpt.isBlank()) {
            return excerpt.trim();
        }
        String textOnly = body
                .replaceAll("(?is)<(script|style)[^>]*>.*?</\\1>", " ")
                .replaceAll("(?s)<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");
        String flattened = textOnly.trim().replaceAll("\\s+", " ");
        return flattened.length() > 200 ? flattened.substring(0, 200) + "…" : flattened;
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private BlogPostSummaryDto toSummaryDto(BlogPost post) {
        return new BlogPostSummaryDto(
                post.getId(), post.getTitle(), post.getSlug(), post.getCategory(),
                post.getExcerpt(), post.getCoverImageUrl(), post.getPublishedAt()
        );
    }

    private BlogPostDto toDto(BlogPost post) {
        return new BlogPostDto(
                post.getId(), post.getTitle(), post.getSlug(), post.getCategory(),
                post.getExcerpt(), post.getBody(), post.getCoverImageUrl(),
                post.getPublishedAt(), post.getUpdatedAt()
        );
    }
}
