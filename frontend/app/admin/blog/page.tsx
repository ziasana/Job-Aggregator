import AdminNav from "@/components/AdminNav";
import Pagination from "@/components/Pagination";
import { getAdminBlogPosts } from "@/lib/adminApi";

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString("en-GB", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

export default async function AdminBlogPage(props: PageProps<"/admin/blog">) {
  const searchParams = await props.searchParams;
  const page = await getAdminBlogPosts(searchParams);

  return (
    <div className="mx-auto max-w-6xl px-4 py-10 sm:px-6">
      <AdminNav active="blog" />

      <div className="mt-6 flex flex-wrap items-start justify-between gap-3">
        <div>
          <h1 className="text-2xl font-extrabold text-navy sm:text-3xl">Manage Blog</h1>
          <p className="mt-1 text-sm text-navy/60">
            {page === null ? "Could not reach the backend." : `${page.totalElements} post${page.totalElements === 1 ? "" : "s"}.`}
          </p>
        </div>
        <a
          href="/admin/blog/new"
          className="rounded-full bg-brand px-4 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-brand-dark"
        >
          New post
        </a>
      </div>

      {page === null ? (
        <div className="mt-8 rounded-2xl border-2 border-dashed border-black/10 bg-white/60 p-12 text-center">
          <p className="text-lg font-bold text-navy">Could not load posts.</p>
        </div>
      ) : page.empty ? (
        <div className="mt-8 rounded-2xl border-2 border-dashed border-black/10 bg-white/60 p-12 text-center">
          <p className="text-lg font-bold text-navy">No posts yet.</p>
          <p className="mt-1 text-sm text-navy/60">Create your first post to see it here.</p>
        </div>
      ) : (
        <>
          <div className="mt-6 overflow-x-auto rounded-2xl border border-black/5 bg-white shadow-sm">
            <table className="w-full min-w-[640px] text-left text-sm">
              <thead>
                <tr className="border-b border-black/5 text-xs font-semibold uppercase tracking-wide text-navy/50">
                  <th className="px-4 py-3">Title</th>
                  <th className="px-4 py-3">Category</th>
                  <th className="px-4 py-3">Published</th>
                  <th className="px-4 py-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {page.content.map((post) => (
                  <tr key={post.id} className="border-b border-black/5 last:border-0 align-top">
                    <td className="px-4 py-3">
                      <div className="font-semibold text-navy">{post.title}</div>
                      <a
                        href={`/blog/${post.slug}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-xs text-brand hover:text-brand-dark"
                      >
                        View live ↗
                      </a>
                    </td>
                    <td className="px-4 py-3 text-navy/70">{post.category ?? "—"}</td>
                    <td className="px-4 py-3 text-navy/70">{formatDate(post.publishedAt)}</td>
                    <td className="px-4 py-3">
                      <div className="flex justify-end gap-2">
                        <a
                          href={`/admin/blog/${post.id}/edit`}
                          className="rounded-full border border-black/10 bg-white px-3.5 py-1.5 text-xs font-semibold text-navy transition hover:border-brand hover:text-brand"
                        >
                          Edit
                        </a>
                        <a
                          href={`/admin/blog/${post.id}/delete`}
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
          <Pagination page={page} searchParams={searchParams} basePath="/admin/blog" itemLabel="post" />
        </>
      )}
    </div>
  );
}
