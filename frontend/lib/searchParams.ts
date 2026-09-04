export type RawSearchParams = { [key: string]: string | string[] | undefined };

/** Next may hand back `string[]` for a repeated param; the API only ever wants one value. */
export function firstValue(value: string | string[] | undefined): string | undefined {
  return Array.isArray(value) ? value[0] : value;
}

/**
 * Builds a query string from the current search params with `overrides`
 * applied on top (e.g. a new `page` or `sortBy`), dropping empty values.
 * Used so pagination/sort links carry forward the active filters.
 */
export function buildHref(
  current: RawSearchParams,
  overrides: Record<string, string | number | undefined>,
  basePath = "/jobs"
): string {
  const params = new URLSearchParams();

  for (const [key, value] of Object.entries(current)) {
    const v = firstValue(value);
    if (v) {
      params.set(key, v);
    }
  }

  for (const [key, value] of Object.entries(overrides)) {
    if (value === undefined || value === "") {
      params.delete(key);
    } else {
      params.set(key, String(value));
    }
  }

  const qs = params.toString();
  return qs ? `${basePath}?${qs}` : basePath;
}
