import JobRow from "@/components/JobRow";
import type { JobSummaryDto } from "@/lib/types";

export default function JobRowList({ jobs }: { jobs: JobSummaryDto[] }) {
  return (
    <ul className="flex flex-col gap-4">
      {jobs.map((job) => (
        <JobRow key={job.id} job={job} />
      ))}
    </ul>
  );
}
