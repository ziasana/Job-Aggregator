import { SOURCE_LABELS, type JobSummaryDto } from "@/lib/types";

function formatSalary(job: JobSummaryDto): string | null {
  if (job.salaryMin == null && job.salaryMax == null) {
    return null;
  }
  const fmt = (n: number) => new Intl.NumberFormat("en-US").format(n);
  if (job.salaryMin != null && job.salaryMax != null) {
    return `${job.currency} ${fmt(job.salaryMin)} – ${fmt(job.salaryMax)}`;
  }
  const n = job.salaryMin ?? job.salaryMax!;
  return `${job.currency} ${fmt(n)}`;
}

function formatDate(iso: string | null): string | null {
  if (!iso) return null;
  return new Date(iso).toLocaleDateString("en-GB", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

export default function JobCard({ job }: { job: JobSummaryDto }) {
  const salary = formatSalary(job);
  const publishedAt = formatDate(job.publishedAt);

  return (
    <li className="rounded-lg border border-black/10 p-4 dark:border-white/15">
      <div className="flex flex-wrap items-start justify-between gap-2">
        <h2 className="text-base font-semibold">{job.title}</h2>
        <div className="flex flex-wrap gap-1">
          {job.sources.map((s) => (
            <span
              key={s}
              className="rounded-full bg-black/5 px-2 py-0.5 text-xs text-black/70 dark:bg-white/10 dark:text-white/70"
            >
              {SOURCE_LABELS[s]}
            </span>
          ))}
        </div>
      </div>

      <p className="mt-1 text-sm text-black/70 dark:text-white/70">
        {[job.company, job.location].filter(Boolean).join(" · ") || "Company/location not provided"}
      </p>

      <div className="mt-2 flex flex-wrap items-center gap-x-4 gap-y-1 text-sm">
        {salary && <span className="font-medium">{salary}</span>}
        {publishedAt && <span className="text-black/50 dark:text-white/50">Posted {publishedAt}</span>}
      </div>

      <a
        href={job.url}
        target="_blank"
        rel="noopener noreferrer"
        className="mt-3 inline-block text-sm font-medium underline hover:no-underline"
      >
        View original posting →
      </a>
    </li>
  );
}
