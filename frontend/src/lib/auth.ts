const TOKEN_KEY = "jobagent_token";
const SESSION_KEY = "jobagent_session";

export function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}

export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}

export function getSessionId(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(SESSION_KEY);
}

export function setSessionId(sessionId: string): void {
  localStorage.setItem(SESSION_KEY, sessionId);
}

export function removeSessionId(): void {
  localStorage.removeItem(SESSION_KEY);
}

export function clearAuth(): void {
  removeToken();
  removeSessionId();
}

export function getTokenFromUrl(): string | null {
  if (typeof window === "undefined") return null;
  const params = new URLSearchParams(window.location.search);
  return params.get("token");
}

export function clearUrlToken(): void {
  if (typeof window === "undefined") return;
  const url = new URL(window.location.href);
  url.searchParams.delete("token");
  window.history.replaceState({}, "", url.toString());
}

export function isAuthenticated(): boolean {
  return !!getToken();
}
