import { notFound } from "next/navigation";
import { getAdminJob } from "@/lib/adminApi";
import { firstValue } from "@/lib/searchParams";
import { SOURCE_LABELS } from "@/lib/types";
import { deleteJob } from "@/app/admin/actions";

export default async function DeleteJobPage(props: PageProps<"/admin/jobs/[id]/delete">) {
  const { id } = await props.params;
  const searchParams = await props.searchParams;
  const returnTo = firstValue(searchParams.returnTo) ?? "/admin";

  const job = await getAdminJob(id);
  if (!job) {
    notFound();
  }

  return (
    <div className="mx-auto max-w-lg px-4 py-16 sm:px-6">
      <div className="rounded-2xl border border-red-200 bg-white p-6 shadow-sm sm:p-8">
        <h1 className="text-xl font-extrabold text-navy">Delete this job?</h1>
        <p className="mt-1 text-sm text-navy/60">This permanently removes the row from the database. It cannot be undone.</p>

        <div className="mt-5 rounded-lg bg-background p-4">
          <div className="font-semibold text-navy">{job.title}</div>
          <div className="mt-0.5 text-sm text-navy/60">
            {[job.company, job.location].filter(Boolean).join(" · ") || "—"}
          </div>
          <div className="mt-1 text-xs text-navy/50">{SOURCE_LABELS[job.source]}</div>
        </div>

        <div className="mt-6 flex justify-end gap-3">
          <a
            href={returnTo}
            className="rounded-full border border-black/10 bg-white px-4 py-2 text-sm font-semibold text-navy transition hover:border-brand hover:text-brand"
          >
            Cancel
          </a>
          <form action={deleteJob}>
            <input type="hidden" name="id" value={job.id} />
            <input type="hidden" name="returnTo" value={returnTo} />
            <button
              type="submit"
              className="rounded-full bg-red-600 px-4 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-red-700"
            >
              Confirm delete
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
