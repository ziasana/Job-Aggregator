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

export default function JobCard({ job }: { job: JobSummaryDto }) {
  const salary = formatSalary(job);
  const publishedAt = formatDate(job.publishedAt);

  return (
    <li className="flex flex-col rounded-2xl bg-white p-5 shadow-sm ring-1 ring-black/5 transition hover:-translate-y-0.5 hover:shadow-lg">
      <div className="flex flex-wrap items-start justify-between gap-2">
        <div className="flex flex-wrap gap-1.5">
          {job.sources.map((s) => (
            <span
              key={s}
              className={`rounded-full px-2.5 py-0.5 text-xs font-semibold ${SOURCE_BADGE_STYLES[s]}`}
            >
              {SOURCE_LABELS[s]}
            </span>
          ))}
        </div>
        {publishedAt && (
          <span className="shrink-0 text-xs font-medium text-navy/40">Posted {publishedAt}</span>
        )}
      </div>

      <h3 className="mt-3 text-base font-bold text-navy">{job.title}</h3>
      <p className="mt-1 text-sm text-navy/60">
        {[job.company, job.location].filter(Boolean).join(" · ") || "Company/location not provided"}
      </p>

      <div className="mt-4 flex items-center justify-between gap-3 border-t border-black/5 pt-4">
        <span className="text-sm font-bold text-brand">{salary ?? "Salary not disclosed"}</span>
        <a
          href={job.url}
          target="_blank"
          rel="noopener noreferrer"
          className="inline-flex shrink-0 items-center gap-1 rounded-full bg-navy px-4 py-2 text-xs font-semibold text-white transition hover:bg-brand"
        >
          View posting
          <span aria-hidden>→</span>
        </a>
      </div>
    </li>
  );
}
