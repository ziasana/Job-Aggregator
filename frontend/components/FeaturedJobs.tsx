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

export default function FeaturedJobs({ jobs }: { jobs: JobSummaryDto[] }) {
  if (jobs.length === 0) {
    return null;
  }

  return (
    <section id="featured-jobs" className="bg-white py-16">
      <div className="mx-auto max-w-6xl px-4 sm:px-6">
        <h2 className="text-center text-2xl font-extrabold text-navy sm:text-3xl">Featured Jobs</h2>
        <p className="mx-auto mt-2 max-w-xl text-center text-sm text-navy/60">
          The most recently published listings across all four sources.
        </p>

        <ul className="mt-10 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {jobs.map((job) => (
            <li
              key={job.id}
              className="flex flex-col rounded-2xl border border-black/5 bg-background p-5 transition hover:-translate-y-0.5 hover:shadow-lg"
            >
              <div className="flex items-center gap-3">
                <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-brand-light text-lg font-extrabold text-brand">
                  {(job.company ?? job.title).charAt(0).toUpperCase()}
                </div>
                <div className="min-w-0">
                  <h3 className="truncate text-sm font-bold text-navy">{job.title}</h3>
                  <p className="truncate text-xs text-navy/60">{job.company ?? "Company not disclosed"}</p>
                </div>
              </div>

              <p className="mt-3 text-xs font-semibold uppercase tracking-wide text-navy/40">
                {job.sources.map((s) => SOURCE_LABELS[s]).join(" · ")}
              </p>

              {job.summary && (
                <p className="mt-2 line-clamp-3 text-sm text-navy/70">{job.summary}</p>
              )}

              <div className="mt-4 flex items-center justify-between gap-3 border-t border-black/5 pt-4">
                <span className="text-sm font-bold text-brand">
                  {formatSalary(job) ?? "Salary not disclosed"}
                </span>
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
          ))}
        </ul>

        <div className="mt-10 text-center">
          <a
            href="/jobs"
            className="inline-flex items-center rounded-full border border-brand px-6 py-2.5 text-sm font-semibold text-brand transition hover:bg-brand hover:text-white"
          >
            View all jobs
          </a>
        </div>
      </div>
    </section>
  );
}
