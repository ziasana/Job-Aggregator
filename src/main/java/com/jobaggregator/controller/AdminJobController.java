package com.jobaggregator.controller;

import com.jobaggregator.dto.AdminJobDto;
import com.jobaggregator.dto.VisibilityUpdateDto;
import com.jobaggregator.entity.JobSource;
import com.jobaggregator.service.AdminJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Admin-only job management (visibility, delete). Protected by HTTP Basic
 * auth - see SecurityConfig.
 */
@RestController
@RequestMapping("/api/admin/jobs")
public class AdminJobController {

    private static final Logger log = LoggerFactory.getLogger(AdminJobController.class);

    private final AdminJobService adminJobService;

    public AdminJobController(AdminJobService adminJobService) {
        this.adminJobService = adminJobService;
    }

    @GetMapping
    public Page<AdminJobDto> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String hidden,
            Pageable pageable
    ) {
        return adminJobService.search(q, parseSource(source), parseHidden(hidden), pageable);
    }

    @GetMapping("/{id}")
    public AdminJobDto getById(@PathVariable UUID id) {
        try {
            return adminJobService.getById(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public AdminJobDto updateVisibility(@PathVariable UUID id, @RequestBody VisibilityUpdateDto body) {
        try {
            return adminJobService.setHidden(id, body.hidden());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        try {
            adminJobService.delete(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private JobSource parseSource(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return JobSource.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.debug("Ignoring unknown source filter '{}'", raw);
            return null;
        }
    }

    private Boolean parseHidden(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "true" -> Boolean.TRUE;
            case "false" -> Boolean.FALSE;
            default -> null;
        };
    }
}
