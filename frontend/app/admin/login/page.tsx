import { login } from "@/app/admin/login/actions";
import { firstValue } from "@/lib/searchParams";

const inputClassName =
  "w-full rounded-lg border border-black/10 bg-white px-3.5 py-2.5 text-sm text-navy placeholder:text-navy/40 outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/20";

export default async function AdminLoginPage(props: PageProps<"/admin/login">) {
  const searchParams = await props.searchParams;
  const returnTo = firstValue(searchParams.returnTo) ?? "/admin";
  const hasError = firstValue(searchParams.error) != null;

  return (
    <div className="mx-auto flex max-w-md flex-col px-4 py-20 sm:px-6">
      <div className="rounded-2xl border border-black/5 bg-white p-8 shadow-sm">
        <h1 className="text-xl font-extrabold text-navy">Admin sign in</h1>
        <p className="mt-1 text-sm text-navy/60">Manage job listing visibility and deletions.</p>

        {hasError && (
          <p className="mt-4 rounded-lg bg-red-50 px-3.5 py-2.5 text-sm font-medium text-red-600">
            Incorrect username or password.
          </p>
        )}

        <form action={login} className="mt-6 flex flex-col gap-4">
          <input type="hidden" name="returnTo" value={returnTo} />
          <div className="flex flex-col gap-1.5">
            <label htmlFor="username" className="text-xs font-semibold text-navy/60">
              Username
            </label>
            <input id="username" name="username" required autoFocus className={inputClassName} />
          </div>
          <div className="flex flex-col gap-1.5">
            <label htmlFor="password" className="text-xs font-semibold text-navy/60">
              Password
            </label>
            <input id="password" name="password" type="password" required className={inputClassName} />
          </div>
          <button
            type="submit"
            className="mt-2 rounded-lg bg-brand py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-brand-dark"
          >
            Sign in
          </button>
        </form>
      </div>
    </div>
  );
}
