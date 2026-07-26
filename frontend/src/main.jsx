import React, { createContext, useContext, useEffect, useState } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { createRoot } from "react-dom/client";
import {
  isAuthenticated,
  clearAuth,
  setSessionId,
  getSessionId,
} from "./lib/auth";
import { getUserInfo, createSession, chat } from "./lib/api";
import LoginPage from "./pages/LoginPage";
import LandingPage from "./pages/LandingPage";
import ChatPage from "./pages/ChatPage";
import DashboardPage from "./pages/DashboardPage";
import GmailCallback from "./pages/GmailCallback";
import "./index.css";

const AuthContext = createContext(null);
const ThemeContext = createContext(null);
const ChatContext = createContext(null);

export function useAuth() {
  return useContext(AuthContext);
}

export function useTheme() {
  return useContext(ThemeContext);
}

export function useChat() {
  return useContext(ChatContext);
}

function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const init = async () => {
      console.log("[Auth] Initializing, checking authentication...");
      if (isAuthenticated()) {
        try {
          console.log("[Auth] Token found, fetching user info...");
          const info = await getUserInfo();
          console.log("[Auth] User loaded:", info.username, info.email);
          setUser(info);
          setIsLoggedIn(true);
        } catch (err) {
          console.log("[Auth] User info fetch failed, clearing auth:", err.message);
          clearAuth();
        }
      } else {
        console.log("[Auth] No token, user not authenticated");
      }
      setLoading(false);
      console.log("[Auth] Auth initialized, loading complete");
    };
    init();
  }, []);

  const logout = () => {
    console.log("[Auth] User logged out");
    clearAuth();
    setUser(null);
    setIsLoggedIn(false);
  };

  const ensureSession = async () => {
    const existing = getSessionId();
    if (existing) {
      console.log("[Auth] Existing session found:", existing);
      return existing;
    }
    console.log("[Auth] Creating new session...");
    const res = await createSession();
    setSessionId(res.sessionId);
    console.log("[Auth] Session ensured:", res.sessionId);
    return res.sessionId;
  };

  return (
    <AuthContext.Provider
      value={{ isLoggedIn, user, loading, logout, ensureSession }}
    >
      {children}
    </AuthContext.Provider>
  );
}

function ThemeProvider({ children }) {
  const [theme, setTheme] = useState(() => {
    return localStorage.getItem("theme") || "dark";
  });

  useEffect(() => {
    const root = document.documentElement;
    if (theme === "light") {
      root.classList.remove("dark");
    } else {
      root.classList.add("dark");
    }
    localStorage.setItem("theme", theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === "dark" ? "light" : "dark"));
  };

  return (
    <ThemeContext.Provider value={{ theme, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
}

function ChatProvider({ children }) {
  const { ensureSession } = useAuth();
  const [messages, setMessages] = useState(() => {
    try {
      const saved = sessionStorage.getItem("chat_messages");
      if (!saved) return [];
      const parsed = JSON.parse(saved);
      if (!Array.isArray(parsed)) return [];
      return parsed.map((m) => ({
        ...m,
        timestamp: m.timestamp ? new Date(m.timestamp) : new Date(),
      }));
    } catch {
      return [];
    }
  });
  const [sending, setSending] = useState(false);

  useEffect(() => {
    sessionStorage.setItem("chat_messages", JSON.stringify(messages));
  }, [messages]);

  const newChat = async () => {
    console.log("[Chat] Creating new chat...");
    try {
      const res = await createSession();
      setSessionId(res.sessionId);
      setMessages([]);
      sessionStorage.removeItem("chat_messages");
      console.log("[Chat] New chat created, session:", res.sessionId);
    } catch (err) {
      console.log("[Chat] Failed to create new chat:", err.message);
    }
  };

  const sendMessage = async (text) => {
    console.log("[Chat] Sending message:", text.substring(0, 50));
    const userMsg = {
      id: crypto.randomUUID(),
      role: "user",
      content: text,
      timestamp: new Date(),
    };
    setMessages((prev) => [...prev, userMsg]);
    setSending(true);

    try {
      const sessionId = await ensureSession();
      console.log("[Chat] Session ID:", sessionId);
      const res = await chat(sessionId, text);
      console.log("[Chat] Response received:", res.response?.substring(0, 50));

      const assistantMsg = {
        id: crypto.randomUUID(),
        role: "assistant",
        content: res.response || "",
        jobs: res.jobs,
        careerEmails: res.careerEmails,
        followUpQuestion: res.followUpQuestion,
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, assistantMsg]);
    } catch (err) {
      console.log("[Chat] Error sending message:", err.message);
      const errorMsg = {
        id: crypto.randomUUID(),
        role: "assistant",
        content: "Something went wrong. Please try again.",
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMsg]);
    } finally {
      setSending(false);
    }
  };

  return (
    <ChatContext.Provider value={{ messages, sending, sendMessage, newChat }}>
      {children}
    </ChatContext.Provider>
  );
}

function ProtectedRoute({ children }) {
  const { isLoggedIn, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center" style={{ background: "var(--page)" }}>
        <div className="animate-pulse" style={{ color: "var(--text-secondary)" }}>Loading...</div>
      </div>
    );
  }

  if (!isLoggedIn) {
    console.log("[ProtectedRoute] User not logged in, redirecting to /");
    return <Navigate to="/" />;
  }

  return children;
}

const fontsLoaded = new Promise((resolve) => {
  if (document.querySelector('[data-fonts="lp"]')) return resolve();
  const links = [
    ["preconnect", "https://fonts.googleapis.com"],
    ["preconnect", "https://fonts.gstatic.com", { crossOrigin: "anonymous" }],
    ["stylesheet", "https://fonts.googleapis.com/css2?family=Bebas+Neue&family=IBM+Plex+Mono:wght@400;500;600&family=IBM+Plex+Sans:wght@400;500;600;700&display=swap"],
  ];
  links.forEach(([rel, href, extra]) => {
    const l = document.createElement("link");
    l.rel = rel; l.href = href; l.dataset.fonts = "lp";
    if (extra) Object.assign(l, extra);
    document.head.append(l);
  });
  resolve();
});

createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      <ThemeProvider>
        <AuthProvider>
          <ChatProvider>
            <Routes>
              <Route path="/" element={<LandingPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/gmail/callback" element={<GmailCallback />} />
              <Route
                path="/chat"
                element={
                  <ProtectedRoute>
                    <ChatPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/dashboard"
                element={
                  <ProtectedRoute>
                    <DashboardPage />
                  </ProtectedRoute>
                }
              />
            </Routes>
          </ChatProvider>
        </AuthProvider>
      </ThemeProvider>
    </BrowserRouter>
  </React.StrictMode>
);
