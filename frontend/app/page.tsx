import Image from "next/image";
import EmptyState from "@/components/EmptyState";
import ErrorState from "@/components/ErrorState";
import FeaturedJobs from "@/components/FeaturedJobs";
import JobList from "@/components/JobList";
import Pagination from "@/components/Pagination";
import SearchFilters from "@/components/SearchFilters";
import TopCategories from "@/components/TopCategories";
import { getJobs, getTopCategories } from "@/lib/api";

export default async function SearchPage(props: PageProps<"/">) {
  const searchParams = await props.searchParams;
  const [page, featured, categories] = await Promise.all([
    getJobs(searchParams),
    getJobs({ sortBy: "date", size: "6" }),
    getTopCategories(8),
  ]);

  return (
    <div>
      <section className="relative overflow-hidden bg-navy text-white">
        {/* hero.jpg: Unsplash License (free for commercial/personal use), photo by
            FOTOGRAFÍA EDITORIAL, https://unsplash.com/photos/FfHWsSOIRp4 */}
        <Image
          src="/hero.jpg"
          alt="A person working on a laptop at an outdoor table"
          fill
          priority
          sizes="100vw"
          className="object-cover"
        />
        <div
          aria-hidden
          className="pointer-events-none absolute inset-0 bg-gradient-to-r from-navy/95 via-navy/70 to-navy/30"
        />
        <div
          aria-hidden
          className="pointer-events-none absolute inset-0 bg-gradient-to-t from-navy/60 via-transparent to-transparent"
        />

        <div className="relative mx-auto max-w-6xl px-4 py-16 sm:px-6 sm:py-24 lg:flex lg:items-center lg:justify-between lg:gap-12">
          <div className="max-w-xl">
            <div className="flex items-center gap-2 text-sm font-semibold text-emerald-300">
              <span className="h-0.5 w-8 bg-emerald-400" />
              Live listings, aggregated in real time
            </div>
            <h1 className="mt-4 text-4xl font-extrabold leading-tight sm:text-5xl">
              Real Jobs, Real Sources, <span className="text-emerald-300">Real Results</span>
            </h1>
            <p className="mt-4 max-w-xl text-base text-white/70">
              Search current job listings pulled live from Arbeitnow, Adzuna, the Bundesagentur für
              Arbeit, and Jobicy — every result links straight back to the original posting.
            </p>

            <dl className="mt-10 flex flex-wrap gap-x-10 gap-y-4">
              <div>
                <dt className="text-3xl font-extrabold sm:text-4xl">
                  4<span className="text-emerald-300">×</span>
                </dt>
                <dd className="text-sm text-white/60">Live data sources</dd>
              </div>
              <div>
                <dt className="text-3xl font-extrabold sm:text-4xl">
                  {page?.totalElements ?? "—"}
                </dt>
                <dd className="text-sm text-white/60">Jobs matching this search</dd>
              </div>
              <div>
                <dt className="text-3xl font-extrabold sm:text-4xl">
                  0<span className="text-emerald-300">s</span>
                </dt>
                <dd className="text-sm text-white/60">Cache — always fetched fresh</dd>
              </div>
            </dl>
          </div>

          <div id="search" className="mt-12 scroll-mt-24 lg:mt-0 lg:w-[420px] lg:shrink-0">
            <SearchFilters searchParams={searchParams} categories={categories ?? []} />
          </div>
        </div>
      </section>

      <section id="listings" className="mx-auto max-w-6xl px-4 py-14 sm:px-6">
        <div className="flex flex-wrap items-end justify-between gap-3">
          <div>
            <h2 className="text-2xl font-extrabold text-navy sm:text-3xl">Job Listings</h2>
            <p className="mt-1 text-sm text-navy/60">
              {page === null
                ? "Live results will appear here once the search service responds."
                : `${page.totalElements} job${page.totalElements === 1 ? "" : "s"} found across all four sources.`}
            </p>
          </div>
        </div>

        <div className="mt-6">
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
      </section>

      <FeaturedJobs jobs={featured?.content ?? []} />

      <TopCategories categories={categories ?? []} />

      <section id="how-it-works" className="bg-white py-16">
        <div className="mx-auto max-w-6xl px-4 sm:px-6">
          <h2 className="text-center text-2xl font-extrabold text-navy sm:text-3xl">How It Works</h2>
          <p className="mx-auto mt-2 max-w-xl text-center text-sm text-navy/60">
            No accounts, no gatekeeping — just a direct path from search to the original job post.
          </p>

          <div className="mt-10 grid grid-cols-1 gap-6 sm:grid-cols-3">
            <HowItWorksStep
              step="1"
              title="Search & filter"
              description="Filter by keyword, location, salary range and job source, then sort by relevance or newest first."
            />
            <HowItWorksStep
              step="2"
              title="Compare four sources"
              description="Results are pulled live from Arbeitnow, Adzuna, the Bundesagentur für Arbeit, and Jobicy in one list."
            />
            <HowItWorksStep
              step="3"
              title="Apply on the original site"
              description="Every card links out to the job's original posting — applications always happen on the source site."
            />
          </div>
        </div>
      </section>
    </div>
  );
}

function HowItWorksStep({
  step,
  title,
  description,
}: {
  step: string;
  title: string;
  description: string;
}) {
  return (
    <div className="rounded-2xl border border-black/5 bg-background p-6 text-center">
      <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-brand-light text-lg font-extrabold text-brand">
        {step}
      </div>
      <h3 className="mt-4 text-base font-bold text-navy">{title}</h3>
      <p className="mt-2 text-sm text-navy/60">{description}</p>
    </div>
  );
}
