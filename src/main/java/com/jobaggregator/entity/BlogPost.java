package com.jobaggregator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

/**
 * Admin-authored article about jobs/the job market (read-only for visitors -
 * no comments or other interaction, per spec). Unrelated to the aggregated
 * {@link NormalizedJob} listings.
 */
@Entity
@Table(name = "blog_post", uniqueConstraints = @UniqueConstraint(columnNames = "slug"))
public class BlogPost {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    private String category;

    @Column(length = 400)
    private String excerpt;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Column(name = "cover_image_url", length = 1024)
    private String coverImageUrl;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BlogPost() {
        // JPA
    }

    public BlogPost(
            String title,
            String slug,
            String category,
            String excerpt,
            String body,
            String coverImageUrl,
            Instant publishedAt,
            Instant updatedAt
    ) {
        this.title = title;
        this.slug = slug;
        this.category = category;
        this.excerpt = excerpt;
        this.body = body;
        this.coverImageUrl = coverImageUrl;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
