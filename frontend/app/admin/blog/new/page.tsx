import BlogPostForm from "@/components/BlogPostForm";
import { firstValue } from "@/lib/searchParams";
import { createPost } from "@/app/admin/blog/actions";

export default async function NewBlogPostPage(props: PageProps<"/admin/blog/new">) {
  const searchParams = await props.searchParams;
  const hasError = firstValue(searchParams.error) != null;

  return (
    <div className="mx-auto max-w-3xl px-4 py-10 sm:px-6">
      <h1 className="text-2xl font-extrabold text-navy sm:text-3xl">New blog post</h1>
      <p className="mt-1 text-sm text-navy/60">Published immediately once you hit &quot;Publish post&quot;.</p>
      <BlogPostForm action={createPost} hasError={hasError} />
    </div>
  );
}
