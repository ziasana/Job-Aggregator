import { notFound } from "next/navigation";
import DOMPurify from "isomorphic-dompurify";
import { getBlogPost } from "@/lib/api";

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString("en-GB", {
    day: "numeric",
    month: "long",
    year: "numeric",
  });
}

export default async function BlogPostPage(props: PageProps<"/blog/[slug]">) {
  const { slug } = await props.params;
  const post = await getBlogPost(slug);
  if (!post) {
    notFound();
  }

  const safeBody = DOMPurify.sanitize(post.body);

  return (
    <article className="mx-auto max-w-3xl px-4 py-10 sm:px-6">
      {/* eslint-disable-next-line @next/next/no-html-link-for-pages -- plain <a>, not next/link, by deliberate project convention (see AGENTS/README) */}
      <a href="/blog" className="text-sm font-semibold text-brand hover:text-brand-dark">
        ← Back to blog
      </a>

      {post.category && (
        <span className="mt-5 block w-fit rounded-full bg-background px-2.5 py-0.5 text-xs font-semibold text-navy/60">
          {post.category}
        </span>
      )}
      <h1 className="mt-3 text-2xl font-extrabold text-navy sm:text-3xl">{post.title}</h1>
      <p className="mt-2 text-sm font-medium text-navy/50">Published {formatDate(post.publishedAt)}</p>

      {post.coverImageUrl && (
        // eslint-disable-next-line @next/next/no-img-element -- external, admin-provided URL; no next/image domain config for this
        <img
          src={post.coverImageUrl}
          alt=""
          className="mt-6 w-full rounded-2xl object-cover shadow-sm"
        />
      )}

      {/* Sanitized (DOMPurify) HTML from the admin rich-text editor */}
      <div className="prose mt-8 max-w-none" dangerouslySetInnerHTML={{ __html: safeBody }} />
    </article>
  );
}
