import { firstValue, type RawSearchParams } from "@/lib/searchParams";
import { SOURCE_LABELS, type JobSource } from "@/lib/types";

const SOURCES: JobSource[] = ["ARBEITNOW", "ADZUNA", "BUNDESAGENTUR", "JOBICY"];

const inputClassName =
  "rounded-md border border-black/15 bg-transparent px-3 py-2 text-sm dark:border-white/20";

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
      className="flex flex-col gap-3 rounded-lg border border-black/10 p-4 md:flex-row md:flex-wrap md:items-end dark:border-white/15"
    >
      <Field label="Keyword" htmlFor="q" className="md:flex-1 md:min-w-48">
        <input
          id="q"
          name="q"
          defaultValue={q}
          placeholder="Job title, company, keyword…"
          className={inputClassName}
        />
      </Field>

      <Field label="Location" htmlFor="location" className="md:w-40">
        <input id="location" name="location" defaultValue={location} className={inputClassName} />
      </Field>

      <Field label="Source" htmlFor="source" className="md:w-48">
        <select id="source" name="source" defaultValue={source} className={inputClassName}>
          <option value="">Any source</option>
          {SOURCES.map((s) => (
            <option key={s} value={s}>
              {SOURCE_LABELS[s]}
            </option>
          ))}
        </select>
      </Field>

      <Field label="Min salary" htmlFor="salaryMin" className="md:w-32">
        <input
          id="salaryMin"
          name="salaryMin"
          type="number"
          inputMode="numeric"
          defaultValue={salaryMin}
          className={inputClassName}
        />
      </Field>

      <Field label="Max salary" htmlFor="salaryMax" className="md:w-32">
        <input
          id="salaryMax"
          name="salaryMax"
          type="number"
          inputMode="numeric"
          defaultValue={salaryMax}
          className={inputClassName}
        />
      </Field>

      <Field label="Sort by" htmlFor="sortBy" className="md:w-40">
        <select id="sortBy" name="sortBy" defaultValue={sortBy} className={inputClassName}>
          <option value="">Best match</option>
          <option value="relevance">Relevance</option>
          <option value="date">Newest</option>
        </select>
      </Field>

      <button
        type="submit"
        className="rounded-md bg-black px-4 py-2 text-sm font-medium text-white hover:bg-black/80 dark:bg-white dark:text-black dark:hover:bg-white/80"
      >
        Search
      </button>
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
    <div className={`flex flex-col gap-1 ${className ?? ""}`}>
      <label htmlFor={htmlFor} className="text-xs font-medium text-black/60 dark:text-white/60">
        {label}
      </label>
      {children}
    </div>
  );
}
