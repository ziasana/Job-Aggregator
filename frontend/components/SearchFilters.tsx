import { firstValue, type RawSearchParams } from "@/lib/searchParams";
import { SOURCE_LABELS, type JobSource } from "@/lib/types";

const SOURCES: JobSource[] = ["ARBEITNOW", "ADZUNA", "BUNDESAGENTUR", "JOBICY"];

const inputClassName =
  "w-full rounded-lg border border-black/10 bg-white px-3.5 py-2.5 text-sm text-navy placeholder:text-navy/40 outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/20";

export default function SearchFilters({ searchParams }: { searchParams: RawSearchParams }) {
  const q = firstValue(searchParams.q) ?? "";
  const location = firstValue(searchParams.location) ?? "";
  const source = firstValue(searchParams.source) ?? "";
  const salaryMin = firstValue(searchParams.salaryMin) ?? "";
  const salaryMax = firstValue(searchParams.salaryMax) ?? "";
  const sortBy = firstValue(searchParams.sortBy) ?? "";

  return (
    <form
      method="GET"
      action="/"
      className="rounded-2xl bg-white p-5 shadow-xl shadow-black/10 ring-1 ring-black/5 sm:p-7"
    >
      <h2 className="text-xl font-bold text-navy sm:text-2xl">
        Search Real Jobs From <span className="text-brand">4 Sources</span>
      </h2>
      <p className="mt-1 text-sm text-navy/60">
        Every listing links back to the original posting — nothing is hosted here.
      </p>

      <div className="mt-5 grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-6">
        <Field label="Keyword" htmlFor="q" className="lg:col-span-2">
          <input
            id="q"
            name="q"
            defaultValue={q}
            placeholder="Job title, company, keyword…"
            className={inputClassName}
          />
        </Field>

        <Field label="Location" htmlFor="location">
          <input
            id="location"
            name="location"
            defaultValue={location}
            placeholder="City, region…"
            className={inputClassName}
          />
        </Field>

        <Field label="Source" htmlFor="source">
          <select id="source" name="source" defaultValue={source} className={inputClassName}>
            <option value="">Any source</option>
            {SOURCES.map((s) => (
              <option key={s} value={s}>
                {SOURCE_LABELS[s]}
              </option>
            ))}
          </select>
        </Field>

        <Field label="Min salary" htmlFor="salaryMin">
          <input
            id="salaryMin"
            name="salaryMin"
            type="number"
            inputMode="numeric"
            defaultValue={salaryMin}
            placeholder="No min"
            className={inputClassName}
          />
        </Field>

        <Field label="Max salary" htmlFor="salaryMax">
          <input
            id="salaryMax"
            name="salaryMax"
            type="number"
            inputMode="numeric"
            defaultValue={salaryMax}
            placeholder="No max"
            className={inputClassName}
          />
        </Field>
      </div>

      <div className="mt-4 flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <Field label="Sort by" htmlFor="sortBy" className="sm:w-48">
          <select id="sortBy" name="sortBy" defaultValue={sortBy} className={inputClassName}>
            <option value="">Best match</option>
            <option value="relevance">Relevance</option>
            <option value="date">Newest first</option>
          </select>
        </Field>

        <button
          type="submit"
          className="rounded-lg bg-brand px-8 py-3 text-sm font-semibold text-white shadow-md shadow-brand/30 transition hover:bg-brand-dark sm:w-auto"
        >
          Search Jobs
        </button>
      </div>
    </form>
  );
}

function Field({
  label,
  htmlFor,
  className,
  children,
}: {
  label: string;
  htmlFor: string;
  className?: string;
  children: React.ReactNode;
}) {
  return (
    <div className={`flex flex-col gap-1.5 ${className ?? ""}`}>
      <label htmlFor={htmlFor} className="text-xs font-semibold text-navy/60">
        {label}
      </label>
      {children}
    </div>
  );
}
