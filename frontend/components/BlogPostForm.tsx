import RichTextEditor from "@/components/RichTextEditor";
import type { BlogPostDto } from "@/lib/types";

const inputClassName =
  "w-full rounded-lg border border-black/10 bg-white px-3.5 py-2.5 text-sm text-navy placeholder:text-navy/40 outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/20";

export default function BlogPostForm({
  action,
  post,
  hasError,
}: {
  action: (formData: FormData) => void;
  post?: BlogPostDto;
  hasError?: boolean;
}) {
  return (
    <form action={action} className="mt-6 flex flex-col gap-4 rounded-2xl border border-black/5 bg-white p-6 sm:p-8">
      {post && <input type="hidden" name="id" value={post.id} />}

      {hasError && (
        <p className="rounded-lg bg-red-50 px-3.5 py-2.5 text-sm font-medium text-red-600">
          Could not save the post. Check the fields and try again.
        </p>
      )}

      <div className="flex flex-col gap-1.5">
        <label htmlFor="title" className="text-xs font-semibold text-navy/60">
          Title
        </label>
        <input
          id="title"
          name="title"
          required
          defaultValue={post?.title}
          placeholder="e.g. 5 Ways the Job Market Is Shifting in 2026"
          className={inputClassName}
        />
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div className="flex flex-col gap-1.5">
          <label htmlFor="category" className="text-xs font-semibold text-navy/60">
            Category
          </label>
          <input
            id="category"
            name="category"
            defaultValue={post?.category ?? ""}
            placeholder="e.g. Career Advice"
            className={inputClassName}
          />
        </div>
        <div className="flex flex-col gap-1.5">
          <label htmlFor="coverImageUrl" className="text-xs font-semibold text-navy/60">
            Cover image URL
          </label>
          <input
            id="coverImageUrl"
            name="coverImageUrl"
            type="url"
            defaultValue={post?.coverImageUrl ?? ""}
            placeholder="https://…"
            className={inputClassName}
          />
        </div>
      </div>

      <div className="flex flex-col gap-1.5">
        <label htmlFor="excerpt" className="text-xs font-semibold text-navy/60">
          Excerpt <span className="font-normal text-navy/40">(optional - auto-generated from the body if left blank)</span>
        </label>
        <textarea
          id="excerpt"
          name="excerpt"
          rows={2}
          maxLength={400}
          defaultValue={post?.excerpt ?? ""}
          placeholder="A short summary shown on the blog list page…"
          className={inputClassName}
        />
      </div>

      <div className="flex flex-col gap-1.5">
        <span className="text-xs font-semibold text-navy/60">Body</span>
        <RichTextEditor name="body" initialContent={post?.body} />
      </div>

      <div className="flex justify-end gap-3">
        <a
          href="/admin/blog"
          className="rounded-full border border-black/10 bg-white px-4 py-2 text-sm font-semibold text-navy transition hover:border-brand hover:text-brand"
        >
          Cancel
        </a>
        <button
          type="submit"
          className="rounded-full bg-brand px-5 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-brand-dark"
        >
          {post ? "Save changes" : "Publish post"}
        </button>
      </div>
    </form>
  );
}
