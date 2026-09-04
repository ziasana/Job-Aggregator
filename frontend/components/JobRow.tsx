import { SOURCE_LABELS, type JobSource, type JobSummaryDto } from "@/lib/types";

const SOURCE_BADGE_STYLES: Record<JobSource, string> = {
  ARBEITNOW: "bg-emerald-500/10 text-emerald-700",
  ADZUNA: "bg-sky-500/10 text-sky-700",
  BUNDESAGENTUR: "bg-amber-500/10 text-amber-700",
  JOBICY: "bg-violet-500/10 text-violet-700",
};

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

export default function JobRow({ job }: { job: JobSummaryDto }) {
  const salary = formatSalary(job);
  const publishedAt = formatDate(job.publishedAt);

  return (
    <li className="flex flex-col gap-4 rounded-2xl border border-black/5 bg-white p-5 transition hover:-translate-y-0.5 hover:shadow-lg sm:flex-row sm:items-center sm:justify-between sm:gap-6">
      <div className="min-w-0 flex-1">
        <div className="flex flex-wrap items-center gap-2">
          <h3 className="text-base font-bold text-navy">{job.title}</h3>
          {job.category && (
            <span className="rounded-full bg-background px-2.5 py-0.5 text-xs font-semibold text-navy/60">
              {job.category}
            </span>
          )}
        </div>

        <p className="mt-1.5 text-sm text-navy/60">
          {[job.company, job.location].filter(Boolean).join("  ·  ") ||
            "Company/location not provided"}
        </p>

        <div className="mt-2.5 flex flex-wrap items-center gap-x-3 gap-y-1.5 text-xs text-navy/50">
          {job.sources.map((s, i) => (
            <span key={s} className="flex items-center gap-3">
              {i > 0 && <span aria-hidden className="text-navy/20">|</span>}
              <span className={`rounded-full px-2.5 py-0.5 font-semibold ${SOURCE_BADGE_STYLES[s]}`}>
                {SOURCE_LABELS[s]}
              </span>
            </span>
          ))}
          {publishedAt && (
            <>
              <span aria-hidden className="text-navy/20">|</span>
              <span>Posted {publishedAt}</span>
            </>
          )}
        </div>
      </div>

      <div className="flex shrink-0 items-center justify-between gap-4 border-t border-black/5 pt-4 sm:flex-col sm:items-end sm:gap-2 sm:border-t-0 sm:border-l sm:border-black/5 sm:pt-0 sm:pl-6">
        <span className="text-sm font-bold text-brand">{salary ?? "Salary not disclosed"}</span>
        <a
          href={job.url}
          target="_blank"
          rel="noopener noreferrer"
          className="inline-flex shrink-0 items-center rounded-full bg-navy px-5 py-2 text-xs font-semibold text-white transition hover:bg-brand"
        >
          View posting
        </a>
      </div>
    </li>
  );
}
