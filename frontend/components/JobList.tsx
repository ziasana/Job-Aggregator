import JobCard from "@/components/JobCard";
import type { JobSummaryDto } from "@/lib/types";

export default function JobList({ jobs }: { jobs: JobSummaryDto[] }) {
  return (
    <ul className="mt-4 flex flex-col gap-3">
      {jobs.map((job) => (
        <JobCard key={job.id} job={job} />
      ))}
    </ul>
  );
}
