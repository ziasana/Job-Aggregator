import { notFound } from "next/navigation";
import { getAdminBlogPost } from "@/lib/adminApi";
import { deletePost } from "@/app/admin/blog/actions";

export default async function DeleteBlogPostPage(props: PageProps<"/admin/blog/[id]/delete">) {
  const { id } = await props.params;

  const post = await getAdminBlogPost(id);
  if (!post) {
    notFound();
  }

  return (
    <div className="mx-auto max-w-lg px-4 py-16 sm:px-6">
      <div className="rounded-2xl border border-red-200 bg-white p-6 shadow-sm sm:p-8">
        <h1 className="text-xl font-extrabold text-navy">Delete this post?</h1>
        <p className="mt-1 text-sm text-navy/60">This permanently removes the post. It cannot be undone.</p>

        <div className="mt-5 rounded-lg bg-background p-4">
          <div className="font-semibold text-navy">{post.title}</div>
          {post.category && <div className="mt-0.5 text-xs text-navy/50">{post.category}</div>}
        </div>

        <div className="mt-6 flex justify-end gap-3">
          <a
            href="/admin/blog"
            className="rounded-full border border-black/10 bg-white px-4 py-2 text-sm font-semibold text-navy transition hover:border-brand hover:text-brand"
          >
            Cancel
          </a>
          <form action={deletePost}>
            <input type="hidden" name="id" value={post.id} />
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
