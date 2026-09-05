"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { createBlogPost, deleteAdminBlogPost, updateBlogPost } from "@/lib/adminApi";
import type { BlogPostInput } from "@/lib/types";

function readInput(formData: FormData): BlogPostInput {
  return {
    title: String(formData.get("title") ?? ""),
    category: String(formData.get("category") ?? ""),
    excerpt: String(formData.get("excerpt") ?? ""),
    body: String(formData.get("body") ?? ""),
    coverImageUrl: String(formData.get("coverImageUrl") ?? ""),
  };
}

export async function createPost(formData: FormData) {
  const post = await createBlogPost(readInput(formData));
  revalidatePath("/admin/blog");
  revalidatePath("/blog");
  if (!post) {
    redirect("/admin/blog/new?error=1");
  }
  redirect("/admin/blog");
}

export async function updatePost(formData: FormData) {
  const id = formData.get("id");
  if (typeof id !== "string") {
    return;
  }

  const post = await updateBlogPost(id, readInput(formData));
  revalidatePath("/admin/blog");
  revalidatePath("/blog");
  if (!post) {
    redirect(`/admin/blog/${id}/edit?error=1`);
  }
  revalidatePath(`/blog/${post.slug}`);
  redirect("/admin/blog");
}

export async function deletePost(formData: FormData) {
  const id = formData.get("id");
  if (typeof id !== "string") {
    return;
  }

  await deleteAdminBlogPost(id);
  revalidatePath("/admin/blog");
  revalidatePath("/blog");
  redirect("/admin/blog");
}
