import { logout } from "@/app/admin/actions";

export default function AdminNav({ active }: { active: "jobs" | "blog" }) {
  return (
    <div className="flex flex-wrap items-center justify-between gap-3">
      <nav className="flex gap-2 text-sm font-semibold">
        <a
          href="/admin"
          className={`rounded-full px-4 py-2 transition ${
            active === "jobs" ? "bg-navy text-white" : "text-navy/60 hover:text-navy"
          }`}
        >
          Jobs
        </a>
        <a
          href="/admin/blog"
          className={`rounded-full px-4 py-2 transition ${
            active === "blog" ? "bg-navy text-white" : "text-navy/60 hover:text-navy"
          }`}
        >
          Blog
        </a>
      </nav>
      <form action={logout}>
        <button
          type="submit"
          className="rounded-full border border-black/10 bg-white px-4 py-2 text-sm font-semibold text-navy transition hover:border-brand hover:text-brand"
        >
          Log out
        </button>
      </form>
    </div>
  );
}
