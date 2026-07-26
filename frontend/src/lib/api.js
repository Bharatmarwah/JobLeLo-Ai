import { getToken, setToken, clearAuth } from "./auth";

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

async function request(path, options = {}, retried = false) {
  const token = getToken();
  const headers = {
    "Content-Type": "application/json",
    ...options.headers,
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  console.log("[API] Request:", options.method || "GET", path, token ? "(with token)" : "(no token)");

  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
  });

  if (!res.ok) {
    if ((res.status === 401 || res.status === 403) && !retried) {
      console.log("[API] Got", res.status, "trying token refresh...");
      try {
        const newToken = await refreshAccessToken();
        if (newToken) {
          console.log("[API] Token refreshed, retrying request");
          headers["Authorization"] = `Bearer ${newToken}`;
          const retryRes = await fetch(`${BASE_URL}${path}`, {
            ...options,
            headers,
          });
          if (!retryRes.ok) {
            const body = await retryRes.text();
            throw new Error(`API error ${retryRes.status}: ${body}`);
          }
          console.log("[API] Retry successful:", retryRes.status, path);
          if (retryRes.status === 204) return null;
          const retryText = await retryRes.text();
          return retryText ? JSON.parse(retryText) : null;
        }
      } catch (refreshErr) {
        console.log("[API] Token refresh failed:", refreshErr.message);
        clearAuth();
        throw new Error("Session expired. Please login again.");
      }
    }

    const body = await res.text();
    console.log("[API] Error:", res.status, body);
    throw new Error(`API error ${res.status}: ${body}`);
  }

  console.log("[API] Response:", res.status, path);
  if (res.status === 204) return null;
  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

export function getGoogleLoginUrl() {
  const url = `${BASE_URL}/public/google/login`;
  console.log("[API] Google login URL:", url);
  return url;
}

export function getGithubLoginUrl() {
  const url = `${BASE_URL}/public/github/login`;
  console.log("[API] GitHub login URL:", url);
  return url;
}

export async function createSession() {
  console.log("[API] Creating session...");
  const res = await request("/api/v1/agent/create-session", {
    method: "POST",
  });
  console.log("[API] Session created:", res.sessionId);
  return res;
}

export async function chat(sessionId, message) {
  console.log("[API] Sending chat message:", message.substring(0, 50));
  const body = { sessionId, message };
  const res = await request("/api/v1/agent/chat", {
    method: "POST",
    body: JSON.stringify(body),
  });
  console.log("[API] Chat response received, jobs:", res.jobs?.length || 0);
  return res;
}

export async function getUserInfo() {
  console.log("[API] Fetching user info...");
  return request("/api/v1/user");
}

export async function getUserJobs(sort = "recent") {
  console.log("[API] Fetching user jobs, sort:", sort);
  const jobs = await request(`/api/v1/user/job?sort=${sort}`);
  console.log("[API] User jobs received:", jobs.length);
  return jobs;
}

export async function getGmailStatus() {
  console.log("[API] Fetching Gmail status...");
  const res = await request("/api/v1/gmail/status");
  console.log("[API] Gmail connected:", res.connected);
  return res;
}

export async function refreshAccessToken() {
  console.log("[API] Refreshing access token...");
  const token = getToken();
  const headers = { "Content-Type": "application/json" };
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  const res = await fetch(`${BASE_URL}/public/refreshtoken`, {
    method: "POST",
    headers,
  });
  if (!res.ok) {
    throw new Error(`Refresh failed: ${res.status}`);
  }
  const data = await res.json();
  if (data.accessToken) {
    setToken(data.accessToken);
    console.log("[API] Token refreshed successfully");
    return data.accessToken;
  }
  throw new Error("No accessToken in refresh response");
}

export async function getGmailAuthUrl() {
  console.log("[API] Getting Gmail auth URL...");
  const res = await request("/api/v1/gmail/connect");
  console.log("[API] Gmail auth URL received");
  return res.authUrl;
}

export async function gmailCallback(code) {
  console.log("[API] Exchanging Gmail authorization code...");
  const res = await request(`/api/v1/gmail/callback?code=${encodeURIComponent(code)}`);
  console.log("[API] Gmail code exchanged successfully");
  return res;
}

export async function removeUserJob(id) {
  console.log("[API] Removing job:", id);
  await request(`/api/v1/user/job/${id}`, { method: "DELETE" });
  console.log("[API] Job removed:", id);
}
