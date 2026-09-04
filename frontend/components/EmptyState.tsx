export default function EmptyState() {
  return (
    <div className="rounded-2xl border-2 border-dashed border-black/10 bg-white/60 p-12 text-center">
      <p className="text-lg font-bold text-navy">No jobs matched your search.</p>
      <p className="mt-1 text-sm text-navy/60">
        Try a broader keyword, or widen your location/salary filters.
      </p>
    </div>
  );
}
