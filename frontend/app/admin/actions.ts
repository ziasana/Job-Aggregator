"use server";

import { cookies } from "next/headers";
import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { ADMIN_SESSION_COOKIE } from "@/lib/adminAuth";
import { deleteAdminJob, setJobHidden } from "@/lib/adminApi";

export async function logout() {
  const cookieStore = await cookies();
  cookieStore.delete({ name: ADMIN_SESSION_COOKIE, path: "/admin" });
  redirect("/admin/login");
}

export async function toggleJobVisibility(formData: FormData) {
  const id = formData.get("id");
  const nextHidden = formData.get("nextHidden");
  const returnTo = formData.get("returnTo");
  if (typeof id !== "string" || typeof nextHidden !== "string") {
    return;
  }

  await setJobHidden(id, nextHidden === "true");
  revalidatePath("/admin");
  redirect(typeof returnTo === "string" && returnTo ? returnTo : "/admin");
}

export async function deleteJob(formData: FormData) {
  const id = formData.get("id");
  const returnTo = formData.get("returnTo");
  if (typeof id !== "string") {
    return;
  }

  await deleteAdminJob(id);
  revalidatePath("/admin");
  redirect(typeof returnTo === "string" && returnTo ? returnTo : "/admin");
}
