"use server";

import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { ADMIN_SESSION_COOKIE, checkCredentials, createSessionToken } from "@/lib/adminAuth";

function safeReturnTo(raw: FormDataEntryValue | null): string {
  return typeof raw === "string" && raw.startsWith("/admin") ? raw : "/admin";
}

export async function login(formData: FormData) {
  const username = formData.get("username");
  const password = formData.get("password");
  const returnTo = safeReturnTo(formData.get("returnTo"));

  if (typeof username !== "string" || typeof password !== "string" || !checkCredentials(username, password)) {
    redirect(`/admin/login?error=1&returnTo=${encodeURIComponent(returnTo)}`);
  }

  const cookieStore = await cookies();
  cookieStore.set(ADMIN_SESSION_COOKIE, await createSessionToken(), {
    httpOnly: true,
    sameSite: "lax",
    secure: process.env.NODE_ENV === "production",
    path: "/admin",
    maxAge: 60 * 60 * 24 * 7,
  });

  redirect(returnTo);
}
