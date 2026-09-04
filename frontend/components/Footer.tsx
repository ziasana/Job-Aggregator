import Logo from "@/components/Logo";

const SOURCES = [
  { name: "Arbeitnow", href: "https://www.arbeitnow.com/" },
  { name: "Adzuna", href: "https://www.adzuna.com/" },
  { name: "Bundesagentur für Arbeit", href: "https://www.arbeitsagentur.de/jobsuche/" },
  { name: "Jobicy", href: "https://jobicy.com/" },
];

export default function Footer() {
  return (
    <footer id="sources" className="mt-16 bg-navy text-white/70">
      <div className="mx-auto max-w-6xl px-4 py-12 sm:px-6">
        <div className="grid grid-cols-1 gap-10 sm:grid-cols-3">
          <div className="sm:col-span-1">
            {/* eslint-disable-next-line @next/next/no-html-link-for-pages -- plain <a>, not next/link, by deliberate project convention (see AGENTS/README) */}
            <a href="/" className="flex items-center gap-2">
              <Logo className="h-8 w-8" />
              <span className="text-base font-bold text-white">
                Job<span className="text-brand">Aggregator</span>
              </span>
            </a>
            <p className="mt-3 text-sm leading-relaxed">
              Job listings are aggregated from third-party sources and always link back to the
              original posting — this site does not host job content itself.
            </p>
          </div>

          <div>
            <h3 className="text-sm font-semibold uppercase tracking-wide text-white">Data sources</h3>
            <ul className="mt-3 flex flex-col gap-2 text-sm">
              {SOURCES.map((s) => (
                <li key={s.name}>
                  <a href={s.href} target="_blank" rel="noopener noreferrer" className="transition hover:text-brand">
                    {s.name}
                  </a>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h3 className="text-sm font-semibold uppercase tracking-wide text-white">Good to know</h3>
            <p className="mt-3 text-sm leading-relaxed">
              The Bundesagentur für Arbeit integration uses a community-documented, unofficial API
              endpoint — it is not an officially sanctioned integration.
            </p>
            <p className="mt-3 text-sm leading-relaxed">
              Unlike the other sources, Jobicy lists remote jobs from anywhere in the world, not
              just Germany.
            </p>
          </div>
        </div>

        <div className="mt-10 border-t border-white/10 pt-6 text-xs text-white/40">
          Job Aggregator — a portfolio project aggregating live listings from four job data
          sources.
        </div>
      </div>
    </footer>
  );
}
