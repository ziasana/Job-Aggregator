import type { CategorySummaryDto } from "@/lib/types";

export default function TopCategories({ categories }: { categories: CategorySummaryDto[] }) {
  if (categories.length === 0) {
    return null;
  }

  return (
    <section id="top-categories" className="bg-background py-16">
      <div className="mx-auto max-w-6xl px-4 sm:px-6">
        <h2 className="text-center text-2xl font-extrabold text-navy sm:text-3xl">Top Categories</h2>
        <p className="mx-auto mt-2 max-w-xl text-center text-sm text-navy/60">
          Browse jobs by category, drawn from each source&apos;s own classification.
        </p>

        <ul className="mt-10 grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
          {categories.map(({ category, count }) => (
            <li key={category}>
              <a
                href={`/?category=${encodeURIComponent(category)}`}
                className="flex flex-col items-center gap-2 rounded-2xl border border-black/5 bg-white p-6 text-center transition hover:-translate-y-0.5 hover:border-brand hover:shadow-lg"
              >
                <span className="flex h-11 w-11 items-center justify-center rounded-full bg-brand-light text-xl" aria-hidden>
                  💼
                </span>
                <span className="text-sm font-bold text-navy">{category}</span>
                <span className="text-xs text-navy/50">
                  {count} job{count === 1 ? "" : "s"}
                </span>
              </a>
            </li>
          ))}
        </ul>
      </div>
    </section>
  );
}
