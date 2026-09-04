export default function Header() {
  return (
    <header className="sticky top-0 z-20 border-b border-black/5 bg-white/95 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-4 sm:px-6">
        {/* eslint-disable-next-line @next/next/no-html-link-for-pages -- plain <a>, not next/link, by deliberate project convention (see AGENTS/README) */}
        <a href="/" className="flex items-center gap-2">
          <span className="flex h-9 w-9 items-center justify-center rounded-full bg-brand text-base font-bold text-white">
            J
          </span>
          <span className="text-lg font-bold tracking-tight text-navy">
            Job<span className="text-brand">Aggregator</span>
          </span>
        </a>

        <nav className="hidden items-center gap-8 text-sm font-medium text-navy/70 sm:flex">
          <a href="#search" className="transition hover:text-brand">
            Search
          </a>
          <a href="#listings" className="transition hover:text-brand">
            Listings
          </a>
          <a href="#how-it-works" className="transition hover:text-brand">
            How it works
          </a>
          <a href="#sources" className="transition hover:text-brand">
            Sources
          </a>
        </nav>

        <a
          href="#search"
          className="rounded-full bg-brand px-4 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-brand-dark sm:px-5"
        >
          Find a job
        </a>
      </div>
    </header>
  );
}
