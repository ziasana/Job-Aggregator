import { buildHref, type RawSearchParams } from "@/lib/searchParams";
import type { Page } from "@/lib/types";

export default function Pagination({
  page,
  searchParams,
  basePath,
  itemLabel = "job",
}: {
  page: Page<unknown>;
  searchParams: RawSearchParams;
  basePath?: string;
  itemLabel?: string;
}) {
  return (
    <nav className="mt-8 flex flex-wrap items-center justify-between gap-3 text-sm">
      <span className="font-medium text-navy/60">
        {page.totalElements} {itemLabel}
        {page.totalElements === 1 ? "" : "s"} found · page {page.number + 1} of{" "}
        {Math.max(page.totalPages, 1)}
      </span>
      <div className="flex gap-2">
        {page.first ? (
          <span className="cursor-not-allowed rounded-full border border-black/10 px-4 py-2 text-navy/30">
            ← Previous
          </span>
        ) : (
          <a
            href={buildHref(searchParams, { page: page.number - 1 }, basePath)}
            className="rounded-full border border-black/10 bg-white px-4 py-2 font-medium text-navy transition hover:border-brand hover:text-brand"
          >
            ← Previous
          </a>
        )}
        {page.last ? (
          <span className="cursor-not-allowed rounded-full border border-black/10 px-4 py-2 text-navy/30">
            Next →
          </span>
        ) : (
          <a
            href={buildHref(searchParams, { page: page.number + 1 }, basePath)}
            className="rounded-full border border-black/10 bg-white px-4 py-2 font-medium text-navy transition hover:border-brand hover:text-brand"
          >
            Next →
          </a>
        )}
      </div>
    </nav>
  );
}
