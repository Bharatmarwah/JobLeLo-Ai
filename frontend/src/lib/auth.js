const TOKEN_KEY = "jobagent_token";
const SESSION_KEY = "jobagent_session";

export function getToken() {
  const token = localStorage.getItem(TOKEN_KEY);
  console.log("[Auth] Token read from localStorage:", token ? "present" : "null");
  return token;
}

export function setToken(token) {
  console.log("[Auth] Token saved to localStorage");
  localStorage.setItem(TOKEN_KEY, token);
}

export function removeToken() {
  console.log("[Auth] Token removed from localStorage");
  localStorage.removeItem(TOKEN_KEY);
}

export function getSessionId() {
  const session = localStorage.getItem(SESSION_KEY);
  console.log("[Auth] Session read from localStorage:", session ? "present" : "null");
  return session;
}

export function setSessionId(sessionId) {
  console.log("[Auth] Session saved to localStorage:", sessionId);
  localStorage.setItem(SESSION_KEY, sessionId);
}

export function removeSessionId() {
  console.log("[Auth] Session removed from localStorage");
  localStorage.removeItem(SESSION_KEY);
}

export function clearAuth() {
  console.log("[Auth] All auth data cleared");
  removeToken();
  removeSessionId();
}

export function getTokenFromUrl() {
  const params = new URLSearchParams(window.location.search);
  const token = params.get("token");
  if (token) {
    console.log("[Auth] Token found in URL");
  }
  return token;
}

export function clearUrlToken() {
  console.log("[Auth] Cleaning token from URL");
  const url = new URL(window.location.href);
  url.searchParams.delete("token");
  window.history.replaceState({}, "", url.toString());
}

export function isAuthenticated() {
  const auth = !!getToken();
  console.log("[Auth] isAuthenticated check:", auth);
  return auth;
}
