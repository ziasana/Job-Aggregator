package com.jobaggregator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "normalized_job",
        uniqueConstraints = @UniqueConstraint(columnNames = {"source", "external_id"})
)
public class NormalizedJob {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobSource source;

    @Column(nullable = false)
    private String title;

    private String company;

    private String location;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "salary_min")
    private BigDecimal salaryMin;

    @Column(name = "salary_max")
    private BigDecimal salaryMax;

    @Column(nullable = false)
    private String currency = "EUR";

    @Column(nullable = false, length = 1024)
    private String url;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "first_seen_at", nullable = false)
    private Instant firstSeenAt;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    @Column(name = "duplicate_group_id")
    private UUID duplicateGroupId;

    protected NormalizedJob() {
        // JPA
    }

    public NormalizedJob(
            String externalId,
            JobSource source,
            String title,
            String company,
            String location,
            String description,
            BigDecimal salaryMin,
            BigDecimal salaryMax,
            String currency,
            String url,
            Instant publishedAt,
            Instant firstSeenAt,
            Instant lastSeenAt
    ) {
        this.externalId = externalId;
        this.source = source;
        this.title = title;
        this.company = company;
        this.location = location;
        this.description = description;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.currency = currency != null ? currency : "EUR";
        this.url = url;
        this.publishedAt = publishedAt;
        this.firstSeenAt = firstSeenAt;
        this.lastSeenAt = lastSeenAt;
    }

    public UUID getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public JobSource getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(BigDecimal salaryMin) {
        this.salaryMin = salaryMin;
    }

    public BigDecimal getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(BigDecimal salaryMax) {
        this.salaryMax = salaryMax;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Instant getFirstSeenAt() {
        return firstSeenAt;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public UUID getDuplicateGroupId() {
        return duplicateGroupId;
    }

    public void setDuplicateGroupId(UUID duplicateGroupId) {
        this.duplicateGroupId = duplicateGroupId;
    }
}
