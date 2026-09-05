package com.jobaggregator.controller;

import com.jobaggregator.dto.BlogPostDto;
import com.jobaggregator.dto.BlogPostRequestDto;
import com.jobaggregator.dto.BlogPostSummaryDto;
import com.jobaggregator.service.BlogPostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;
import java.util.UUID;

/** Admin-only blog management (create/edit/delete). Protected by HTTP Basic auth - see SecurityConfig. */
@RestController
@RequestMapping("/api/admin/blog")
public class AdminBlogController {

    private final BlogPostService blogPostService;

    public AdminBlogController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @GetMapping
    public Page<BlogPostSummaryDto> list(Pageable pageable) {
        return blogPostService.list(pageable);
    }

    @GetMapping("/{id}")
    public BlogPostDto getById(@PathVariable UUID id) {
        try {
            return blogPostService.getById(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BlogPostDto create(@Valid @RequestBody BlogPostRequestDto request) {
        return blogPostService.create(request);
    }

    @PutMapping("/{id}")
    public BlogPostDto update(@PathVariable UUID id, @Valid @RequestBody BlogPostRequestDto request) {
        try {
            return blogPostService.update(id, request);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        try {
            blogPostService.delete(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
