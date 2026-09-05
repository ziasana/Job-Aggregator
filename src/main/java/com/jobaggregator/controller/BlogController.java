package com.jobaggregator.controller;

import com.jobaggregator.dto.BlogPostDto;
import com.jobaggregator.dto.BlogPostSummaryDto;
import com.jobaggregator.service.BlogPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

/** Public, read-only blog API - no comments or other interaction, per spec. */
@RestController
@RequestMapping("/api/blog")
public class BlogController {

    private final BlogPostService blogPostService;

    public BlogController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @GetMapping
    public Page<BlogPostSummaryDto> list(Pageable pageable) {
        return blogPostService.list(pageable);
    }

    @GetMapping("/{slug}")
    public BlogPostDto getBySlug(@PathVariable String slug) {
        try {
            return blogPostService.getBySlug(slug);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
