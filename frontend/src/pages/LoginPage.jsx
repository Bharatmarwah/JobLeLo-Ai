import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../main";
import { getGoogleLoginUrl, getGithubLoginUrl } from "../lib/api";
import { setToken, getTokenFromUrl, clearUrlToken } from "../lib/auth";

export default function LoginPage() {
  const { isLoggedIn, loading } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const token = getTokenFromUrl();
    if (token) {
      setToken(token);
      clearUrlToken();
      window.location.href = "/chat";
      return;
    }
  }, []);

  useEffect(() => {
    if (!loading && isLoggedIn) {
      navigate("/chat");
    }
  }, [loading, isLoggedIn, navigate]);

  const handleGoogleLogin = () => {
    window.location.href = getGoogleLoginUrl();
  };

  const handleGithubLogin = () => {
    window.location.href = getGithubLoginUrl();
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center" style={{ background: "var(--page)" }}>
        <div className="animate-pulse" style={{ color: "var(--text-secondary)" }}>Loading...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex" style={{ background: "var(--page)" }}>
      <div className="flex-1 flex items-center justify-center p-8">
        <div className="w-full max-w-sm">
          <div className="mb-8">
            <span className="flex items-center gap-3 mb-6" style={{ color: "var(--ink)" }}>
              <span className="w-2.5 h-6 rounded-sm" style={{ background: "var(--marigold)", transform: "skewX(-12deg)" }} />
              <span style={{ fontFamily: "'IBM Plex Mono', monospace", fontSize: 22, letterSpacing: "0.5px" }}>JobLelo</span>
            </span>
            <h1 className="text-2xl font-bold mb-1" style={{ color: "var(--ink)" }}>
              Sign in
            </h1>
            <p style={{ color: "var(--ink-soft)", fontSize: 14 }}>
              to start your search
            </p>
          </div>

          <div className="space-y-3">
            <button
              onClick={handleGoogleLogin}
              className="flex items-center justify-center gap-3 w-full px-4 py-3 rounded font-medium transition-all text-sm"
              style={{ background: "var(--card)", color: "var(--text-primary)", border: "1px solid var(--line)" }}
              onMouseEnter={(e) => { e.currentTarget.style.background = "var(--muted)"; }}
              onMouseLeave={(e) => { e.currentTarget.style.background = "var(--card)"; }}
            >
              <svg className="w-5 h-5" viewBox="0 0 24 24">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 01-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z"/>
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
              </svg>
              Continue with Google
            </button>

            <button
              onClick={handleGithubLogin}
              className="flex items-center justify-center gap-3 w-full px-4 py-3 rounded font-medium transition-all text-sm"
              style={{ background: "var(--card)", color: "var(--text-primary)", border: "1px solid var(--line)" }}
              onMouseEnter={(e) => { e.currentTarget.style.background = "var(--muted)"; }}
              onMouseLeave={(e) => { e.currentTarget.style.background = "var(--card)"; }}
            >
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
              </svg>
              Continue with GitHub
            </button>
          </div>

          <p className="text-center mt-8 text-sm" style={{ color: "var(--text-secondary)" }}>
            <span style={{ fontFamily: "'IBM Plex Mono', monospace", fontSize: 11, letterSpacing: "0.3px" }}>
              One search. Every source. One place.
            </span>
          </p>
        </div>
      </div>

      <div className="hidden lg:flex flex-1 items-center justify-center" style={{ background: "var(--ink)" }}>
        <div className="max-w-sm text-center px-8">
          <div style={{ fontFamily: "'IBM Plex Mono', monospace", fontSize: 11, letterSpacing: "2.5px", color: "var(--marigold)", textTransform: "uppercase", marginBottom: 20 }}>
            <span className="inline-block w-2 h-2 rounded-full mr-2" style={{ background: "var(--marigold)", boxShadow: "0 0 0 0 rgba(244,166,35,0.6)", animation: "pulse 2s infinite" }} />
            AI-Powered Search
          </div>
          <h2 style={{ fontFamily: "'Bebas Neue', sans-serif", fontSize: 52, lineHeight: 0.98, letterSpacing: "0.5px", color: "var(--page)", margin: "0 0 16px" }}>
            One search.<br />Everything<br /><span style={{ color: "var(--marigold)" }}>arrives.</span>
          </h2>
          <p style={{ fontSize: 15, color: "rgba(251,247,239,0.7)", lineHeight: 1.6 }}>
            JobLelo runs your search across sources and your inbox at the same time — then ranks what actually fits you.
          </p>
          <div style={{ marginTop: 28, height: 1, backgroundImage: "repeating-linear-gradient(90deg, rgba(251,247,239,0.1) 0 4px, transparent 4px 8px)" }} />
          <p style={{ fontFamily: "'IBM Plex Mono', monospace", fontSize: 10.5, letterSpacing: "1px", color: "rgba(251,247,239,0.4)", marginTop: 20, textTransform: "uppercase" }}>
            Status: Ready
          </p>
        </div>
      </div>
    </div>
  );
}