export default function Footer() {
  return (
    <footer className="mt-12 border-t border-black/10 py-6 text-sm text-black/60 dark:border-white/15 dark:text-white/50">
      <div className="mx-auto max-w-4xl px-4">
        <p>
          Job listings are aggregated from third-party sources and always link back to the
          original posting — this site does not host job content itself.
        </p>
        <ul className="mt-2 flex flex-wrap gap-x-4 gap-y-1">
          <li>
            <a
              href="https://www.arbeitnow.com/"
              target="_blank"
              rel="noopener noreferrer"
              className="underline hover:no-underline"
            >
              Arbeitnow
            </a>
          </li>
          <li>
            <a
              href="https://www.adzuna.com/"
              target="_blank"
              rel="noopener noreferrer"
              className="underline hover:no-underline"
            >
              Adzuna
            </a>
          </li>
          <li>
            <a
              href="https://www.arbeitsagentur.de/jobsuche/"
              target="_blank"
              rel="noopener noreferrer"
              className="underline hover:no-underline"
            >
              Bundesagentur für Arbeit
            </a>
          </li>
        </ul>
        <p className="mt-2">
          The Bundesagentur für Arbeit integration uses a community-documented, unofficial API
          endpoint — it is not an officially sanctioned integration.
        </p>
      </div>
    </footer>
  );
}
