import { buildHref, type RawSearchParams } from "@/lib/searchParams";
import type { Page, JobSummaryDto } from "@/lib/types";

export default function Pagination({
  page,
  searchParams,
}: {
  page: Page<JobSummaryDto>;
  searchParams: RawSearchParams;
}) {
  return (
    <nav className="mt-6 flex flex-wrap items-center justify-between gap-2 text-sm">
      <span className="text-black/60 dark:text-white/60">
        {page.totalElements} job{page.totalElements === 1 ? "" : "s"} · page {page.number + 1} of{" "}
        {Math.max(page.totalPages, 1)}
      </span>
      <div className="flex gap-2">
        {page.first ? (
          <span className="rounded-md border border-black/10 px-3 py-1.5 text-black/30 dark:border-white/15 dark:text-white/30">
            ← Previous
          </span>
        ) : (
          <a
            href={buildHref(searchParams, { page: page.number - 1 })}
            className="rounded-md border border-black/10 px-3 py-1.5 hover:bg-black/5 dark:border-white/15 dark:hover:bg-white/10"
          >
            ← Previous
          </a>
        )}
        {page.last ? (
          <span className="rounded-md border border-black/10 px-3 py-1.5 text-black/30 dark:border-white/15 dark:text-white/30">
            Next →
          </span>
        ) : (
          <a
            href={buildHref(searchParams, { page: page.number + 1 })}
            className="rounded-md border border-black/10 px-3 py-1.5 hover:bg-black/5 dark:border-white/15 dark:hover:bg-white/10"
          >
            Next →
          </a>
        )}
      </div>
    </nav>
  );
}
