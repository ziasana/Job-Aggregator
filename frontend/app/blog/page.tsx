import EmptyState from "@/components/EmptyState";
import ErrorState from "@/components/ErrorState";
import Pagination from "@/components/Pagination";
import { getBlogPosts } from "@/lib/api";

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString("en-GB", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

export default async function BlogPage(props: PageProps<"/blog">) {
  const searchParams = await props.searchParams;
  const page = await getBlogPosts(searchParams);

  return (
    <div className="mx-auto max-w-6xl px-4 py-10 sm:px-6">
      <div>
        <h1 className="text-2xl font-extrabold text-navy sm:text-3xl">Career &amp; Job Market Blog</h1>
        <p className="mt-1 text-sm text-navy/60">
          Advice, trends, and insights for job seekers - written by the Job Aggregator team.
        </p>
      </div>

      <div className="mt-8">
        {page === null ? (
          <ErrorState />
        ) : page.empty ? (
          <EmptyState />
        ) : (
          <>
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
              {page.content.map((post) => (
                <a
                  key={post.id}
                  href={`/blog/${post.slug}`}
                  className="flex flex-col overflow-hidden rounded-2xl border border-black/5 bg-white shadow-sm transition hover:-translate-y-0.5 hover:shadow-lg"
                >
                  {post.coverImageUrl ? (
                    // eslint-disable-next-line @next/next/no-img-element -- external, admin-provided URLs; no next/image domain config for this
                    <img src={post.coverImageUrl} alt="" className="h-44 w-full object-cover" />
                  ) : (
                    <div className="flex h-44 w-full items-center justify-center bg-brand-light text-brand">
                      <span className="text-sm font-semibold">Job Aggregator Blog</span>
                    </div>
                  )}
                  <div className="flex flex-1 flex-col p-5">
                    {post.category && (
                      <span className="mb-2 w-fit rounded-full bg-background px-2.5 py-0.5 text-xs font-semibold text-navy/60">
                        {post.category}
                      </span>
                    )}
                    <h2 className="text-base font-bold text-navy">{post.title}</h2>
                    {post.excerpt && (
                      <p className="mt-2 line-clamp-3 flex-1 text-sm text-navy/70">{post.excerpt}</p>
                    )}
                    <p className="mt-3 text-xs font-medium text-navy/50">{formatDate(post.publishedAt)}</p>
                  </div>
                </a>
              ))}
            </div>
            <Pagination page={page} searchParams={searchParams} basePath="/blog" itemLabel="post" />
          </>
        )}
      </div>
    </div>
  );
}
