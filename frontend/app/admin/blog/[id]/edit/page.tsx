import { notFound } from "next/navigation";
import BlogPostForm from "@/components/BlogPostForm";
import { getAdminBlogPost } from "@/lib/adminApi";
import { firstValue } from "@/lib/searchParams";
import { updatePost } from "@/app/admin/blog/actions";

export default async function EditBlogPostPage(props: PageProps<"/admin/blog/[id]/edit">) {
  const { id } = await props.params;
  const searchParams = await props.searchParams;
  const hasError = firstValue(searchParams.error) != null;

  const post = await getAdminBlogPost(id);
  if (!post) {
    notFound();
  }

  return (
    <div className="mx-auto max-w-3xl px-4 py-10 sm:px-6">
      <h1 className="text-2xl font-extrabold text-navy sm:text-3xl">Edit blog post</h1>
      <p className="mt-1 text-sm text-navy/60">The URL slug (/blog/{post.slug}) stays fixed once published.</p>
      <BlogPostForm action={updatePost} post={post} hasError={hasError} />
    </div>
  );
}
