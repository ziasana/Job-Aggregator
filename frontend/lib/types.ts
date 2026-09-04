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
