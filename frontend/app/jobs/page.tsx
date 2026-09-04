import EmptyState from "@/components/EmptyState";
import ErrorState from "@/components/ErrorState";
import JobRowList from "@/components/JobRowList";
import Pagination from "@/components/Pagination";
import SearchFilters from "@/components/SearchFilters";
import { getJobs, getTopCategories } from "@/lib/api";

export default async function JobsPage(props: PageProps<"/jobs">) {
  const searchParams = await props.searchParams;
  const [page, categories] = await Promise.all([
    getJobs(searchParams),
    getTopCategories(20),
  ]);

  return (
    <div className="mx-auto max-w-6xl px-4 py-10 sm:px-6">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <h1 className="text-2xl font-extrabold text-navy sm:text-3xl">Job Listings</h1>
          <p className="mt-1 text-sm text-navy/60">
            {page === null
              ? "Live results will appear here once the search service responds."
              : `${page.totalElements} job${page.totalElements === 1 ? "" : "s"} found across all four sources.`}
          </p>
        </div>
      </div>

      <div className="mt-8 grid grid-cols-1 gap-8 lg:grid-cols-[280px_1fr]">
        <aside className="lg:sticky lg:top-24 lg:h-fit">
          <SearchFilters searchParams={searchParams} categories={categories ?? []} variant="sidebar" />
        </aside>

        <div>
          {page === null ? (
            <ErrorState />
          ) : page.empty ? (
            <EmptyState />
          ) : (
            <>
              <JobRowList jobs={page.content} />
              <Pagination page={page} searchParams={searchParams} />
            </>
          )}
        </div>
      </div>
    </div>
  );
}
