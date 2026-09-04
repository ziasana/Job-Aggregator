export default function EmptyState() {
  return (
    <div className="mt-8 rounded-lg border border-dashed border-black/15 p-8 text-center text-black/60 dark:border-white/20 dark:text-white/60">
      <p className="font-medium">No jobs matched your search.</p>
      <p className="mt-1 text-sm">Try a broader keyword, or widen your location/salary filters.</p>
    </div>
  );
}
