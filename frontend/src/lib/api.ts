import {
  ChatRequest,
  ChatResponse,
  SessionResponse,
  TokenResponse,
  UserInfoResponse,
  UserJobResponse,
} from "./types";
import { getToken } from "./auth";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

async function request<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...((options.headers as Record<string, string>) || {}),
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
  });

  if (!res.ok) {
    const body = await res.text();
    throw new Error(`API error ${res.status}: ${body}`);
  }

  return res.json();
}

export async function createSession(): Promise<SessionResponse> {
  return request<SessionResponse>("/api/v1/agent/create-session", {
    method: "POST",
  });
}

export async function chat(
  sessionId: string,
  message: string
): Promise<ChatResponse> {
  const body: ChatRequest = { sessionId, message };
  return request<ChatResponse>("/api/v1/agent/chat", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export async function getUserInfo(): Promise<UserInfoResponse> {
  return request<UserInfoResponse>("/api/v1/user");
}

export async function getUserJobs(
  sort: "recent" | "relevance" = "recent"
): Promise<UserJobResponse[]> {
  return request<UserJobResponse[]>(`/api/v1/user/job?sort=${sort}`);
}

export function getGoogleLoginUrl(): string {
  return `${BASE_URL}/public/google/login`;
}

export function getGithubLoginUrl(): string {
  return `${BASE_URL}/public/github/login`;
}

export async function refreshToken(): Promise<TokenResponse> {
  const token = getToken();
  return request<TokenResponse>("/public/refreshtoken", {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
}
