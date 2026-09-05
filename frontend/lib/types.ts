export type JobSource = "ARBEITNOW" | "ADZUNA" | "BUNDESAGENTUR" | "JOBICY";

export interface JobSummaryDto {
  id: string;
  title: string;
  company: string | null;
  location: string | null;
  salaryMin: number | null;
  salaryMax: number | null;
  currency: string;
  source: JobSource;
  sources: JobSource[];
  url: string;
  publishedAt: string | null;
  category: string | null;
  summary: string | null;
}

export interface AdminJobDto {
  id: string;
  title: string;
  company: string | null;
  location: string | null;
  category: string | null;
  summary: string | null;
  salaryMin: number | null;
  salaryMax: number | null;
  currency: string;
  source: JobSource;
  url: string;
  publishedAt: string | null;
  firstSeenAt: string;
  lastSeenAt: string;
  hidden: boolean;
}

export interface BlogPostSummaryDto {
  id: string;
  title: string;
  slug: string;
  category: string | null;
  excerpt: string | null;
  coverImageUrl: string | null;
  publishedAt: string;
}

export interface BlogPostDto extends BlogPostSummaryDto {
  body: string;
  updatedAt: string;
}

export interface BlogPostInput {
  title: string;
  category: string;
  excerpt: string;
  body: string;
  coverImageUrl: string;
}

export interface CategorySummaryDto {
  category: string;
  count: number;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export const SOURCE_LABELS: Record<JobSource, string> = {
  ARBEITNOW: "Arbeitnow",
  ADZUNA: "Adzuna",
  BUNDESAGENTUR: "Bundesagentur für Arbeit",
  JOBICY: "Jobicy",
};
