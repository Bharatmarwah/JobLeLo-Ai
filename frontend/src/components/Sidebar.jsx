import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { useAuth, useTheme } from "../main";
import { getUserInfo } from "../lib/api";

export default function Sidebar() {
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const pathname = useLocation().pathname;
  const [showInfo, setShowInfo] = useState(false);
  const [infoData, setInfoData] = useState(null);
  const [loadingInfo, setLoadingInfo] = useState(false);

  const handleUserInfo = async () => {
    setLoadingInfo(true);
    try {
      const data = await getUserInfo();
      setInfoData(data);
      setShowInfo(true);
    } catch (_) {
    } finally {
      setLoadingInfo(false);
    }
  };

  const navItems = [
    {
      label: "Chat",
      href: "/chat",
      icon: (
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M7.5 8.25h9m-9 3H12m-9.75 1.51c0 1.6 1.123 2.994 2.707 3.227 1.129.166 2.27.293 3.423.379.35.026.67.21.865.501L12 21l2.755-4.133a1.14 1.14 0 01.865-.501 48.172 48.172 0 003.423-.379c1.584-.233 2.707-1.626 2.707-3.228V6.741c0-1.602-1.123-2.995-2.707-3.228A48.394 48.394 0 0012 3c-2.392 0-4.744.175-7.043.513C3.373 3.746 2.25 5.14 2.25 6.741v6.018z" />
        </svg>
      ),
    },
    {
      label: "My Jobs",
      href: "/dashboard",
      icon: (
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M20.25 14.15v4.25c0 1.094-.787 2.036-1.872 2.18-2.087.277-4.216.42-6.378.42s-4.291-.143-6.378-.42c-1.085-.144-1.872-1.086-1.872-2.18v-4.25m16.5 0a2.18 2.18 0 00.75-1.661V8.706c0-1.081-.768-2.015-1.837-2.175a48.114 48.114 0 00-3.413-.387m4.5 8.006c-.194.165-.42.295-.673.38A23.978 23.978 0 0112 15.75c-2.648 0-5.195-.429-7.577-1.22a2.016 2.016 0 01-.673-.38m0 0A2.18 2.18 0 013 12.489V8.706c0-1.081.768-2.015 1.837-2.175a48.111 48.111 0 013.413-.387m7.5 0V5.25A2.25 2.25 0 0013.5 3h-3a2.25 2.25 0 00-2.25 2.25v.894m7.5 0a48.667 48.667 0 00-7.5 0" />
        </svg>
      ),
    },
  ];

  return (
    <aside
      className="w-56 h-full flex flex-col shrink-0 border-r"
      style={{
        background: "var(--page)",
        borderColor: "var(--line)",
      }}
    >
      <div className="h-14 flex items-center px-4" style={{ borderBottom: "1px dashed var(--line)" }}>
        <Link to="/chat" className="flex items-center gap-2.5 no-underline" style={{ color: "var(--ink)" }}>
          <span className="w-1.5 h-5 shrink-0" style={{ background: "var(--marigold)", transform: "skewX(-12deg)", borderRadius: 2 }} />
          <span style={{ fontFamily: "'Bebas Neue', sans-serif", fontSize: 22, letterSpacing: "0.5px" }}>JobLelo</span>
        </Link>
      </div>

      <nav className="flex-1 px-3 space-y-0.5 pt-2">
        {navItems.map((item) => {
          const active = pathname === item.href;
          return (
            <Link
              key={item.href}
              to={item.href}
              className="flex items-center gap-3 px-3 py-2.5 text-sm no-underline transition-all"
              style={{
                fontFamily: "'IBM Plex Mono', monospace",
                fontSize: 12.5,
                letterSpacing: "0.4px",
                borderRadius: 4,
                color: active ? "var(--ink)" : "var(--text-secondary)",
                background: active ? "var(--muted)" : "transparent",
              }}
              onMouseEnter={(e) => {
                if (!active) { e.currentTarget.style.background = "var(--muted)"; e.currentTarget.style.color = "var(--ink)"; }
              }}
              onMouseLeave={(e) => {
                if (!active) { e.currentTarget.style.background = "transparent"; e.currentTarget.style.color = "var(--text-secondary)"; }
              }}
            >
              {item.icon}
              <span>{item.label}</span>
            </Link>
          );
        })}
      </nav>

      {user && (
        <div className="p-3" style={{ borderTop: "1px dashed var(--line)" }}>
          <div className="flex items-center gap-2.5 px-2 py-1.5">
            {user.profileUrl ? (
              <img src={user.profileUrl} alt="" className="w-7 h-7 rounded-full shrink-0 object-cover" />
            ) : (
              <div className="w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold shrink-0"
                style={{ background: "var(--muted)", color: "var(--text-secondary)" }}>
                {(user.username || user.email || "?")[0].toUpperCase()}
              </div>
            )}
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium truncate" style={{ color: "var(--text-primary)" }}>
                {user.username || "User"}
              </p>
              <p className="text-[11px] truncate" style={{ color: "var(--text-secondary)" }}>{user.email}</p>
            </div>
            <div className="flex items-center gap-0.5">
              <button
                onClick={toggleTheme}
                className="w-6 h-6 flex items-center justify-center transition-colors"
                style={{ borderRadius: 4, color: "var(--text-secondary)" }}
                onMouseEnter={(e) => e.currentTarget.style.background = "var(--muted)"}
                onMouseLeave={(e) => e.currentTarget.style.background = "transparent"}
                title={theme === "dark" ? "Light mode" : "Dark mode"}
              >
                {theme === "dark" ? (
                  <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 3v2.25m6.364.386l-1.591 1.591M21 12h-2.25m-.386 6.364l-1.591-1.591M12 18.75V21m-4.773-4.227l-1.591 1.591M5.25 12H3m4.227-4.773L5.636 5.636M15.75 12a3.75 3.75 0 11-6 0 3.75 3.75 0 016 0z" />
                  </svg>
                ) : (
                  <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M21.752 15.002A9.718 9.718 0 0118 15.75c-5.385 0-9.75-4.365-9.75-9.75 0-1.33.266-2.597.748-3.752A9.753 9.753 0 003 11.25C3 16.635 7.365 21 12.75 21a9.753 9.753 0 009.002-5.998z" />
                  </svg>
                )}
              </button>
              <button
                onClick={handleUserInfo}
                className="w-6 h-6 flex items-center justify-center transition-colors"
                style={{ borderRadius: 4, color: "var(--text-secondary)" }}
                onMouseEnter={(e) => e.currentTarget.style.background = "var(--muted)"}
                onMouseLeave={(e) => e.currentTarget.style.background = "transparent"}
                title="User info"
              >
                <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M11.25 11.25l.041-.02a.75.75 0 011.063.852l-.708 2.836a.75.75 0 001.063.853l.041-.021M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9-3.75h.008v.008H12V8.25z" />
                </svg>
              </button>
              <button
                onClick={() => logout()}
                className="w-6 h-6 flex items-center justify-center transition-colors"
                style={{ borderRadius: 4, color: "var(--text-secondary)" }}
                onMouseEnter={(e) => e.currentTarget.style.background = "var(--muted)"}
                onMouseLeave={(e) => e.currentTarget.style.background = "transparent"}
                title="Logout"
              >
                <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15m3 0l3-3m0 0l-3-3m3 3H9" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      )}

      {showInfo && infoData && (
        <>
          <div className="fixed inset-0 z-40" style={{ background: "rgba(0,0,0,0.5)" }} onClick={() => setShowInfo(false)} />
          <div className="fixed inset-0 flex items-center justify-center z-50 pointer-events-none">
            <div className="w-[500px] pointer-events-auto p-8 border" style={{ background: "var(--card)", borderColor: "var(--line)", color: "var(--text-primary)", borderRadius: 6 }}>
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-lg font-semibold" style={{ color: "var(--text-primary)" }}>Account Info</h3>
                <button onClick={() => setShowInfo(false)} className="transition-colors" style={{ color: "var(--text-secondary)" }}>
                  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
              <div className="space-y-5 text-sm" style={{ color: "var(--text-secondary)" }}>
                <div><span className="text-xs uppercase tracking-wider" style={{ color: "var(--text-secondary)" }}>Username</span><p className="text-base font-medium mt-1" style={{ color: "var(--text-primary)" }}>{infoData.username || "—"}</p></div>
                <div><span className="text-xs uppercase tracking-wider" style={{ color: "var(--text-secondary)" }}>Email</span><p className="text-base mt-1" style={{ color: "var(--text-primary)" }}>{infoData.email || "—"}</p></div>
                <div><span className="text-xs uppercase tracking-wider" style={{ color: "var(--text-secondary)" }}>Profile URL</span><p className="text-base truncate mt-1" style={{ color: "var(--accent)" }}>{infoData.profileUrl || "—"}</p></div>
                <div><span className="text-xs uppercase tracking-wider" style={{ color: "var(--text-secondary)" }}>Account created</span><p className="text-base mt-1" style={{ color: "var(--text-primary)" }}>{infoData.createdAt ? new Date(infoData.createdAt).toLocaleDateString() : "—"}</p></div>
              </div>
            </div>
          </div>
        </>
      )}
    </aside>
  );
}