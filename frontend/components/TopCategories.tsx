import type { CategorySummaryDto } from "@/lib/types";

/**
 * Categories are real, dynamic free-text strings from each source's own
 * classification (German and English, including Arbeitnow's noisy tag
 * bag) - there's no fixed enum to map exactly. Matched by keyword instead,
 * checked in order, with a generic fallback for anything unmatched rather
 * than fabricating precision we don't have.
 */
const EXACT_ICONS: Record<string, string> = {
  it: "💻",
  hr: "🧑‍💼",
};

const CATEGORY_ICONS: { keywords: string[]; icon: string }[] = [
  { keywords: ["techniker", "technical"], icon: "🔧" },
  { keywords: ["software", "entwickl", "developer", "informatik", "it-", "programm", "engineering"], icon: "💻" },
  { keywords: ["sozial"], icon: "🤝" },
  { keywords: ["erzieher", "pädagog", "education", "teacher"], icon: "🎓" },
  { keywords: ["sales", "vertrieb"], icon: "📈" },
  { keywords: ["marketing", "kommunikation", "communication"], icon: "📣" },
  { keywords: ["customer", "support", "kundenservice"], icon: "🎧" },
  { keywords: ["gesundheit", "pflege", "health", "medizin"], icon: "🩺" },
  { keywords: ["buchhaltung", "finanz", "accounting", "finance"], icon: "💰" },
  { keywords: ["logistik", "logistics", "lager"], icon: "📦" },
  { keywords: ["gastronomie", "catering", "restaurant"], icon: "🍽️" },
  { keywords: ["berater", "consult"], icon: "🧭" },
  { keywords: ["recht", "legal", "jura"], icon: "⚖️" },
  { keywords: ["personal", "human resources"], icon: "🧑‍💼" },
  { keywords: ["remote"], icon: "🌐" },
  { keywords: ["sonstige", "allgemein", "general", "other", "misc"], icon: "🗂️" },
];

const DEFAULT_ICON = "💼";

function getCategoryIcon(category: string): string {
  const lower = category.toLowerCase().trim();
  if (EXACT_ICONS[lower]) {
    return EXACT_ICONS[lower];
  }
  return CATEGORY_ICONS.find(({ keywords }) => keywords.some((k) => lower.includes(k)))?.icon ?? DEFAULT_ICON;
}

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
                href={`/jobs?category=${encodeURIComponent(category)}`}
                className="flex flex-col items-center gap-2 rounded-2xl border border-black/5 bg-white p-6 text-center transition hover:-translate-y-0.5 hover:border-brand hover:shadow-lg"
              >
                <span className="flex h-11 w-11 items-center justify-center rounded-full bg-brand-light text-xl" aria-hidden>
                  {getCategoryIcon(category)}
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
