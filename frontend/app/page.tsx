import EmptyState from "@/components/EmptyState";
import ErrorState from "@/components/ErrorState";
import JobList from "@/components/JobList";
import Pagination from "@/components/Pagination";
import SearchFilters from "@/components/SearchFilters";
import { getJobs } from "@/lib/api";

export default async function SearchPage(props: PageProps<"/">) {
  const searchParams = await props.searchParams;
  const page = await getJobs(searchParams);

  return (
    <div className="mx-auto max-w-4xl px-4 py-8">
      <h1 className="text-2xl font-semibold">Job Aggregator</h1>
      <p className="mt-1 text-sm text-black/60 dark:text-white/60">
        Search job listings aggregated from Arbeitnow, Adzuna, and the Bundesagentur für Arbeit.
      </p>

      <div className="mt-6">
        <SearchFilters searchParams={searchParams} />
      </div>

      {page === null ? (
        <ErrorState />
      ) : page.empty ? (
        <EmptyState />
      ) : (
        <>
          <JobList jobs={page.content} />
          <Pagination page={page} searchParams={searchParams} />
        </>
      )}
    </div>
  );
}
