import { firstValue, type RawSearchParams } from "@/lib/searchParams";
import type { BlogPostDto, BlogPostSummaryDto, CategorySummaryDto, JobSummaryDto, Page } from "@/lib/types";

const API_BASE_URL = process.env.API_BASE_URL ?? "http://localhost:8080";

const FORWARDED_PARAMS = [
  "q",
  "location",
  "source",
  "category",
  "salaryMin",
  "salaryMax",
  "sortBy",
  "page",
  "size",
] as const;

/**
 * Fetches search results from the Spring Boot backend. Runs server-side
 * only (Server Component), so the browser never calls the backend directly
 * and no CORS configuration is needed there.
 *
 * Returns `null` on any network failure (e.g. backend not running) so the
 * page can render a friendly error instead of crashing.
 */
export async function getJobs(searchParams: RawSearchParams): Promise<Page<JobSummaryDto> | null> {
  const qs = new URLSearchParams();
  for (const key of FORWARDED_PARAMS) {
    const value = firstValue(searchParams[key]);
    if (value) {
      qs.set(key, value);
    }
  }

  try {
    const response = await fetch(`${API_BASE_URL}/api/jobs?${qs.toString()}`, {
      cache: "no-store",
    });
    if (!response.ok) {
      return null;
    }
    return (await response.json()) as Page<JobSummaryDto>;
  } catch {
    return null;
  }
}

/**
 * Fetches the top job categories with counts, for the homepage's "Top
 * Categories" section. Same failure handling as `getJobs`: `null` on any
 * network failure rather than throwing.
 */
export async function getTopCategories(limit = 8): Promise<CategorySummaryDto[] | null> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/jobs/categories?limit=${limit}`, {
      cache: "no-store",
    });
    if (!response.ok) {
      return null;
    }
    return (await response.json()) as CategorySummaryDto[];
  } catch {
    return null;
  }
}

/** Public, read-only blog listing (no comments/interaction - see BlogController). */
export async function getBlogPosts(searchParams: RawSearchParams): Promise<Page<BlogPostSummaryDto> | null> {
  const qs = new URLSearchParams();
  for (const key of ["page", "size"] as const) {
    const value = firstValue(searchParams[key]);
    if (value) {
      qs.set(key, value);
    }
  }

  try {
    const response = await fetch(`${API_BASE_URL}/api/blog?${qs.toString()}`, {
      cache: "no-store",
    });
    if (!response.ok) {
      return null;
    }
    return (await response.json()) as Page<BlogPostSummaryDto>;
  } catch {
    return null;
  }
}

export async function getBlogPost(slug: string): Promise<BlogPostDto | null> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/blog/${slug}`, {
      cache: "no-store",
    });
    if (!response.ok) {
      return null;
    }
    return (await response.json()) as BlogPostDto;
  } catch {
    return null;
  }
}
