/**
 * The magnifying glass lens is a 2x2 grid in the exact same four colors used
 * for the Arbeitnow/Adzuna/Bundesagentur/Jobicy source badges elsewhere in
 * the app (see JobRow.tsx) - "searching across four real aggregated
 * sources", not an arbitrary decorative icon.
 */
export default function Logo({ className = "h-9 w-9" }: { className?: string }) {
  return (
    <svg viewBox="0 0 40 40" className={className} aria-hidden="true">
      <rect width="40" height="40" rx="10" fill="#14213a" />
      <rect x="13" y="13" width="4.5" height="4.5" rx="1" fill="#10b981" />
      <rect x="18.5" y="13" width="4.5" height="4.5" rx="1" fill="#0ea5e9" />
      <rect x="13" y="18.5" width="4.5" height="4.5" rx="1" fill="#f59e0b" />
      <rect x="18.5" y="18.5" width="4.5" height="4.5" rx="1" fill="#8b5cf6" />
      <circle cx="18" cy="18" r="9" fill="none" stroke="#ffffff" strokeWidth="3" />
      <line x1="24.5" y1="24.5" x2="31" y2="31" stroke="#ffffff" strokeWidth="3.5" strokeLinecap="round" />
    </svg>
  );
}
