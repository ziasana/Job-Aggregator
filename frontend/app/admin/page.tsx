import AdminNav from "@/components/AdminNav";
import Pagination from "@/components/Pagination";
import { buildHref, firstValue } from "@/lib/searchParams";
import { getAdminJobs } from "@/lib/adminApi";
import { SOURCE_LABELS, type JobSource } from "@/lib/types";
import { toggleJobVisibility } from "@/app/admin/actions";

const SOURCES: JobSource[] = ["ARBEITNOW", "ADZUNA", "BUNDESAGENTUR", "JOBICY"];

const inputClassName =
  "w-full rounded-lg border border-black/10 bg-white px-3.5 py-2.5 text-sm text-navy placeholder:text-navy/40 outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/20";

function formatDateTime(iso: string): string {
  return new Date(iso).toLocaleString("en-GB", {
    day: "numeric",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export default async function AdminPage(props: PageProps<"/admin">) {
  const searchParams = await props.searchParams;
  const page = await getAdminJobs(searchParams);

  const q = firstValue(searchParams.q) ?? "";
  const source = firstValue(searchParams.source) ?? "";
  const hidden = firstValue(searchParams.hidden) ?? "";
  const returnTo = buildHref(searchParams, {}, "/admin");

  return (
    <div className="mx-auto max-w-6xl px-4 py-10 sm:px-6">
      <AdminNav active="jobs" />

      <div className="mt-6">
        <h1 className="text-2xl font-extrabold text-navy sm:text-3xl">Manage Jobs</h1>
        <p className="mt-1 text-sm text-navy/60">
          {page === null
            ? "Could not reach the backend."
            : `${page.totalElements} row${page.totalElements === 1 ? "" : "s"} across all sources (including hidden and duplicate entries).`}
        </p>
      </div>

      <form
        method="GET"
        action="/admin"
        className="mt-6 grid grid-cols-1 gap-3 rounded-2xl border border-black/5 bg-white p-5 sm:grid-cols-[1fr_auto_auto_auto]"
      >
        <input
          name="q"
          defaultValue={q}
          placeholder="Search by title or company…"
          className={inputClassName}
        />
        <select name="source" defaultValue={source} className={inputClassName}>
          <option value="">Any source</option>
          {SOURCES.map((s) => (
            <option key={s} value={s}>
              {SOURCE_LABELS[s]}
            </option>
          ))}
        </select>
        <select name="hidden" defaultValue={hidden} className={inputClassName}>
          <option value="">Visible + hidden</option>
          <option value="false">Visible only</option>
          <option value="true">Hidden only</option>
        </select>
        <button
          type="submit"
          className="rounded-lg bg-brand px-5 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-brand-dark"
        >
          Filter
        </button>
      </form>

      {page === null ? (
        <div className="mt-8 rounded-2xl border-2 border-dashed border-black/10 bg-white/60 p-12 text-center">
          <p className="text-lg font-bold text-navy">Could not load jobs.</p>
          <p className="mt-1 text-sm text-navy/60">Check that the backend is running and admin credentials are correct.</p>
        </div>
      ) : page.empty ? (
        <div className="mt-8 rounded-2xl border-2 border-dashed border-black/10 bg-white/60 p-12 text-center">
          <p className="text-lg font-bold text-navy">No jobs matched.</p>
        </div>
      ) : (
        <>
          <div className="mt-6 overflow-x-auto rounded-2xl border border-black/5 bg-white shadow-sm">
            <table className="w-full min-w-[820px] text-left text-sm">
              <thead>
                <tr className="border-b border-black/5 text-xs font-semibold uppercase tracking-wide text-navy/50">
                  <th className="px-4 py-3">Job</th>
                  <th className="px-4 py-3">Source</th>
                  <th className="px-4 py-3">Last seen</th>
                  <th className="px-4 py-3">Status</th>
                  <th className="px-4 py-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {page.content.map((job) => (
                  <tr key={job.id} className="border-b border-black/5 last:border-0 align-top">
                    <td className="px-4 py-3">
                      <div className="font-semibold text-navy">{job.title}</div>
                      <div className="mt-0.5 text-xs text-navy/60">
                        {[job.company, job.location].filter(Boolean).join(" · ") || "—"}
                      </div>
                    </td>
                    <td className="px-4 py-3 text-navy/70">{SOURCE_LABELS[job.source]}</td>
                    <td className="px-4 py-3 text-navy/70">{formatDateTime(job.lastSeenAt)}</td>
                    <td className="px-4 py-3">
                      {job.hidden ? (
                        <span className="rounded-full bg-amber-500/10 px-2.5 py-0.5 text-xs font-semibold text-amber-700">
                          Hidden
                        </span>
                      ) : (
                        <span className="rounded-full bg-emerald-500/10 px-2.5 py-0.5 text-xs font-semibold text-emerald-700">
                          Visible
                        </span>
                      )}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex justify-end gap-2">
                        <form action={toggleJobVisibility}>
                          <input type="hidden" name="id" value={job.id} />
                          <input type="hidden" name="nextHidden" value={(!job.hidden).toString()} />
                          <input type="hidden" name="returnTo" value={returnTo} />
                          <button
                            type="submit"
                            className="rounded-full border border-black/10 bg-white px-3.5 py-1.5 text-xs font-semibold text-navy transition hover:border-brand hover:text-brand"
                          >
                            {job.hidden ? "Show" : "Hide"}
                          </button>
                        </form>
                        <a
                          href={`/admin/jobs/${job.id}/delete?returnTo=${encodeURIComponent(returnTo)}`}
                          className="rounded-full border border-red-200 bg-white px-3.5 py-1.5 text-xs font-semibold text-red-600 transition hover:border-red-400 hover:bg-red-50"
                        >
                          Delete
                        </a>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <Pagination page={page} searchParams={searchParams} basePath="/admin" />
        </>
      )}
    </div>
  );
}
