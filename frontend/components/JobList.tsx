import JobCard from "@/components/JobCard";
import type { JobSummaryDto } from "@/lib/types";

export default function JobList({ jobs }: { jobs: JobSummaryDto[] }) {
  return (
    <ul className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {jobs.map((job) => (
        <JobCard key={job.id} job={job} />
      ))}
    </ul>
  );
}
