import { firstValue, type RawSearchParams } from "@/lib/searchParams";
import type { JobSummaryDto, Page } from "@/lib/types";

const API_BASE_URL = process.env.API_BASE_URL ?? "http://localhost:8080";

const FORWARDED_PARAMS = ["q", "location", "source", "salaryMin", "salaryMax", "sortBy", "page", "size"] as const;

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
