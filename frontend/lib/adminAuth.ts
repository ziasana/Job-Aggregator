const encoder = new TextEncoder();

export const ADMIN_SESSION_COOKIE = "admin_session";

async function hmacKey(secret: string): Promise<CryptoKey> {
  return crypto.subtle.importKey(
    "raw",
    encoder.encode(secret),
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"]
  );
}

function toHex(buffer: ArrayBuffer): string {
  return Array.from(new Uint8Array(buffer))
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("");
}

/**
 * Session token = HMAC-SHA256(admin password, admin username), hex-encoded.
 * Deterministic (no random session id, no server-side session store) - it's
 * really just "prove you knew the credentials at login time", which is
 * enough for a single-admin-account setup. It doesn't expire on its own; the
 * cookie's own maxAge (see login/actions.ts) is what bounds a session, and
 * rotating ADMIN_PASSWORD invalidates every outstanding cookie at once.
 */
export async function createSessionToken(): Promise<string> {
  const secret = process.env.ADMIN_PASSWORD ?? "admin";
  const username = process.env.ADMIN_USERNAME ?? "admin";
  const key = await hmacKey(secret);
  const signature = await crypto.subtle.sign("HMAC", key, encoder.encode(username));
  return toHex(signature);
}

function timingSafeEqual(a: string, b: string): boolean {
  if (a.length !== b.length) {
    return false;
  }
  let diff = 0;
  for (let i = 0; i < a.length; i++) {
    diff |= a.charCodeAt(i) ^ b.charCodeAt(i);
  }
  return diff === 0;
}

export async function verifySessionToken(token: string | undefined): Promise<boolean> {
  if (!token) {
    return false;
  }
  return timingSafeEqual(token, await createSessionToken());
}

export function checkCredentials(username: string, password: string): boolean {
  const expectedUsername = process.env.ADMIN_USERNAME ?? "admin";
  const expectedPassword = process.env.ADMIN_PASSWORD ?? "admin";
  return timingSafeEqual(username, expectedUsername) && timingSafeEqual(password, expectedPassword);
}
