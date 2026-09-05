import { firstValue, type RawSearchParams } from "@/lib/searchParams";
import type { AdminJobDto, BlogPostDto, BlogPostInput, BlogPostSummaryDto, Page } from "@/lib/types";

const API_BASE_URL = process.env.API_BASE_URL ?? "http://localhost:8080";

/**
 * Basic-auth header for the backend's /api/admin/** endpoints. Uses the same
 * credentials the frontend's own /admin routes are gated behind (see
 * middleware.ts) - never sent to the browser, only attached server-side.
 */
function adminAuthHeader(): string {
  const username = process.env.ADMIN_USERNAME ?? "admin";
  const password = process.env.ADMIN_PASSWORD ?? "admin";
  return `Basic ${Buffer.from(`${username}:${password}`).toString("base64")}`;
}

const FORWARDED_PARAMS = ["q", "source", "hidden", "page", "size"] as const;

export async function getAdminJobs(searchParams: RawSearchParams): Promise<Page<AdminJobDto> | null> {
  const qs = new URLSearchParams();
  for (const key of FORWARDED_PARAMS) {
    const value = firstValue(searchParams[key]);
    if (value) {
      qs.set(key, value);
    }
  }

  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/jobs?${qs.toString()}`, {
      headers: { Authorization: adminAuthHeader() },
      cache: "no-store",
    });
    if (!response.ok) {
      return null;
    }
    return (await response.json()) as Page<AdminJobDto>;
  } catch {
    return null;
  }
}

export async function getAdminJob(id: string): Promise<AdminJobDto | null> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/jobs/${id}`, {
      headers: { Authorization: adminAuthHeader() },
      cache: "no-store",
    });
    if (!response.ok) {
      return null;
    }
    return (await response.json()) as AdminJobDto;
  } catch {
    return null;
  }
}

export async function setJobHidden(id: string, hidden: boolean): Promise<boolean> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/jobs/${id}`, {
      method: "PATCH",
      headers: {
        Authorization: adminAuthHeader(),
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ hidden }),
      cache: "no-store",
    });
    return response.ok;
  } catch {
    return false;
  }
}

export async function deleteAdminJob(id: string): Promise<boolean> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/jobs/${id}`, {
      method: "DELETE",
      headers: { Authorization: adminAuthHeader() },
      cache: "no-store",
    });
    return response.ok;
  } catch {
    return false;
  }
}

export async function getAdminBlogPosts(searchParams: RawSearchParams): Promise<Page<BlogPostSummaryDto> | null> {
  const qs = new URLSearchParams();
  for (const key of ["page", "size"] as const) {
    const value = firstValue(searchParams[key]);
    if (value) {
      qs.set(key, value);
    }
  }

  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/blog?${qs.toString()}`, {
      headers: { Authorization: adminAuthHeader() },
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

export async function getAdminBlogPost(id: string): Promise<BlogPostDto | null> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/blog/${id}`, {
      headers: { Authorization: adminAuthHeader() },
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

export async function createBlogPost(input: BlogPostInput): Promise<BlogPostDto | null> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/blog`, {
      method: "POST",
      headers: {
        Authorization: adminAuthHeader(),
        "Content-Type": "application/json",
      },
      body: JSON.stringify(input),
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

export async function updateBlogPost(id: string, input: BlogPostInput): Promise<BlogPostDto | null> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/blog/${id}`, {
      method: "PUT",
      headers: {
        Authorization: adminAuthHeader(),
        "Content-Type": "application/json",
      },
      body: JSON.stringify(input),
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

export async function deleteAdminBlogPost(id: string): Promise<boolean> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/blog/${id}`, {
      method: "DELETE",
      headers: { Authorization: adminAuthHeader() },
      cache: "no-store",
    });
    return response.ok;
  } catch {
    return false;
  }
}
