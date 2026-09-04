import { firstValue, type RawSearchParams } from "@/lib/searchParams";
import { SOURCE_LABELS, type CategorySummaryDto, type JobSource } from "@/lib/types";

const SOURCES: JobSource[] = ["ARBEITNOW", "ADZUNA", "BUNDESAGENTUR", "JOBICY"];

const inputClassName =
  "w-full rounded-lg border border-black/10 bg-white px-3.5 py-2.5 text-sm text-navy placeholder:text-navy/40 outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/20";

export default function SearchFilters({
  searchParams,
  categories,
  variant = "hero",
}: {
  searchParams: RawSearchParams;
  categories: CategorySummaryDto[];
  variant?: "hero" | "sidebar";
}) {
  const q = firstValue(searchParams.q) ?? "";
  const location = firstValue(searchParams.location) ?? "";
  const source = firstValue(searchParams.source) ?? "";
  const category = firstValue(searchParams.category) ?? "";
  const salaryMin = firstValue(searchParams.salaryMin) ?? "";
  const salaryMax = firstValue(searchParams.salaryMax) ?? "";
  const sortBy = firstValue(searchParams.sortBy) ?? "";
  const isSidebar = variant === "sidebar";

  return (
    <form
      method="GET"
      action="/jobs"
      className={
        isSidebar
          ? "rounded-2xl border border-black/5 bg-white p-5 shadow-sm sm:p-6"
          : "rounded-3xl bg-white p-6 shadow-2xl shadow-black/20 ring-1 ring-black/5 sm:p-8"
      }
    >
      <h2 className={isSidebar ? "text-base font-extrabold text-navy" : "text-xl font-extrabold text-navy sm:text-2xl"}>
        {isSidebar ? (
          "Filter Jobs"
        ) : (
          <>
            Grow Your Career With <span className="text-brand">Job Aggregator</span>
          </>
        )}
      </h2>

      <div className="mt-5">
        <input
          id="q"
          name="q"
          defaultValue={q}
          placeholder="Search job keywords…"
          className={inputClassName}
        />
      </div>

      <div className={isSidebar ? "mt-3 grid grid-cols-1 gap-3" : "mt-3 grid grid-cols-1 gap-3 sm:grid-cols-2"}>
        <Field label="Job Category" htmlFor="category">
          <select id="category" name="category" defaultValue={category} className={inputClassName}>
            <option value="">Any category</option>
            {categories.map(({ category: c }) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
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

        <Field label="Sort by" htmlFor="sortBy">
          <select id="sortBy" name="sortBy" defaultValue={sortBy} className={inputClassName}>
            <option value="">Best match</option>
            <option value="relevance">Relevance</option>
            <option value="date">Newest first</option>
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

      <button
        type="submit"
        className="mt-5 w-full rounded-lg bg-brand py-3 text-sm font-semibold text-white shadow-md shadow-brand/30 transition hover:bg-brand-dark"
      >
        Search Result
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
    <div className={`flex flex-col gap-1.5 ${className ?? ""}`}>
      <label htmlFor={htmlFor} className="text-xs font-semibold text-navy/60">
        {label}
      </label>
      {children}
    </div>
  );
}
