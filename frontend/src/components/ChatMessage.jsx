import { useEffect, useState } from "react";
import { useAuth, useChat } from "../main";
import JobList from "./JobList";
import CareerEmailCard from "./CareerEmailCard";

const AVATAR_URL = (import.meta.env.VITE_API_URL || "http://localhost:8080") + "/api/v1/user/avatar";

function formatTime(ts) {
  const diff = Date.now() - ts.getTime();
  if (diff < 60000) return "just now";
  if (diff < 3600000) return `${Math.floor(diff / 60000)}m ago`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}h ago`;
  return ts.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}

function detectLinks(text) {
  const urlRegex = /(https?:\/\/[^\s]+)/g;
  const parts = [];
  let last = 0;
  let match;
  while ((match = urlRegex.exec(text)) !== null) {
    if (match.index > last) parts.push({ t: "text", v: text.slice(last, match.index) });
    parts.push({ t: "link", v: match[0] });
    last = urlRegex.lastIndex;
  }
  if (last < text.length) parts.push({ t: "text", v: text.slice(last) });
  return parts.length ? parts : [{ t: "text", v: text }];
}

export default function ChatMessageBubble({ message }) {
  const isUser = message.role === "user";
  const { sendMessage } = useChat();
  const { user } = useAuth();
  const [visible, setVisible] = useState(false);
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    requestAnimationFrame(() => setVisible(true));
  }, []);

  const handleCopy = () => {
    navigator.clipboard.writeText(message.content).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 1500);
    });
  };

  return (
    <div
      className={`flex mb-5 transition-all duration-300 ease-out ${
        visible ? "opacity-100 translate-y-0" : "opacity-0 translate-y-3"
      }`}
      style={{ justifyContent: isUser ? "flex-end" : "flex-start" }}
    >
      {isUser ? (
        <div className="max-w-[80%]">
          <div className="flex items-center gap-2 mb-1.5 justify-end">
            <span className="text-[11px]" style={{ color: "var(--text-secondary)", opacity: 0.5 }}>{formatTime(message.timestamp)}</span>
            <span className="text-xs font-medium" style={{ color: "var(--text-secondary)" }}>You</span>
            {user?.profileUrl ? (
              <img src={AVATAR_URL} alt="" referrerPolicy="no-referrer"
                className="w-6 h-6 rounded-full shrink-0"
                style={{ objectFit: "cover", display: "block" }}
                onError={(e) => {
                  e.target.style.display = "none";
                  const parent = e.target.parentElement;
                  if (parent) parent.textContent = (user?.username || user?.email || "U")[0].toUpperCase();
                }} />
            ) : (
              <div className="w-6 h-6 rounded-full flex items-center justify-center text-[10px] font-bold shrink-0" style={{ background: "var(--marigold)", color: "var(--ink)" }}>
                {(user?.username || user?.email || "U")[0].toUpperCase()}
              </div>
            )}
          </div>
          <div
            className="px-4 py-3 relative group"
            style={{
              background: "var(--marigold)",
              color: "var(--ink)",
              borderRadius: "6px 6px 2px 6px",
            }}
          >
            <div className="text-base whitespace-pre-wrap leading-relaxed" style={{ letterSpacing: "0.01em" }}>
              {detectLinks(message.content).map((p, i) =>
                p.t === "link" ? (
                  <a key={i} href={p.v} target="_blank" rel="noopener noreferrer" style={{ color: "var(--ink)", textDecoration: "underline", textUnderlineOffset: 2 }}>
                    {p.v}
                  </a>
                ) : (
                  <span key={i}>{p.v}</span>
                )
              )}
            </div>
            <button
              onClick={handleCopy}
              className="absolute -top-2 -right-2 w-6 h-6 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity border"
              style={{ background: "var(--card)", borderColor: "var(--line)" }}
              onMouseEnter={(e) => { e.currentTarget.style.background = "var(--muted)"; }}
              onMouseLeave={(e) => { e.currentTarget.style.background = "var(--card)"; }}
              title={copied ? "Copied!" : "Copy message"}
            >
              {copied ? (
                <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2} style={{ color: "var(--emerald)" }}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12.75l6 6 9-13.5" />
                </svg>
              ) : (
                <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2} style={{ color: "var(--text-secondary)" }}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M15.666 3.888A2.25 2.25 0 0013.5 2.25h-3c-1.03 0-1.9.693-2.166 1.638m7.332 0c.055.194.084.4.084.612v0a.75.75 0 01-.75.75H9a.75.75 0 01-.75-.75v0c0-.212.03-.418.084-.612m7.332 0c.646.049 1.288.11 1.927.184 1.1.128 1.907 1.077 1.907 2.185V19.5a2.25 2.25 0 01-2.25 2.25H6.75A2.25 2.25 0 014.5 19.5V6.257c0-1.108.806-2.057 1.907-2.185a48.208 48.208 0 011.927-.184" />
                </svg>
              )}
            </button>
          </div>
          {message.jobs && message.jobs.length > 0 && (
            <div className="mt-2" style={{ animation: "fadeIn 0.3s ease-out" }}>
              <JobList jobs={message.jobs} />
            </div>
          )}
        </div>
      ) : (
        <div className="flex gap-3 max-w-[80%]">
          <div className="shrink-0 pt-0">
            <div className="w-6 h-6 rounded-full flex items-center justify-center" style={{ background: "var(--muted)" }}>
              <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2} style={{ color: "var(--ink-soft)" }}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09z" />
              </svg>
            </div>
          </div>
          <div className="min-w-0 flex-1">
            <div className="flex items-center gap-2 mb-1.5">
              <span className="text-xs font-medium" style={{ color: "var(--text-secondary)" }}>AI</span>
              <span className="text-[11px]" style={{ color: "var(--text-secondary)", opacity: 0.5 }}>{formatTime(message.timestamp)}</span>
            </div>
            <div
              className="px-4 py-3 relative group"
              style={{
                background: "var(--muted)",
                color: "var(--text-primary)",
                borderRadius: "6px 6px 6px 2px",
              }}
            >
              <div className="text-base whitespace-pre-wrap leading-relaxed" style={{ letterSpacing: "0.01em" }}>
                {detectLinks(message.content).map((p, i) =>
                  p.t === "link" ? (
                    <a key={i} href={p.v} target="_blank" rel="noopener noreferrer" style={{ color: "var(--accent)", textDecoration: "underline", textUnderlineOffset: 2 }}>
                      {p.v}
                    </a>
                  ) : (
                    <span key={i}>{p.v}</span>
                  )
                )}
              </div>
              <button
                onClick={handleCopy}
                className="absolute -top-2 -right-2 w-6 h-6 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity border"
                style={{ background: "var(--card)", borderColor: "var(--line)" }}
                onMouseEnter={(e) => { e.currentTarget.style.background = "var(--muted)"; }}
                onMouseLeave={(e) => { e.currentTarget.style.background = "var(--card)"; }}
                title={copied ? "Copied!" : "Copy message"}
              >
                {copied ? (
                  <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2} style={{ color: "var(--emerald)" }}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12.75l6 6 9-13.5" />
                  </svg>
                ) : (
                  <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2} style={{ color: "var(--text-secondary)" }}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15.666 3.888A2.25 2.25 0 0013.5 2.25h-3c-1.03 0-1.9.693-2.166 1.638m7.332 0c.055.194.084.4.084.612v0a.75.75 0 01-.75.75H9a.75.75 0 01-.75-.75v0c0-.212.03-.418.084-.612m7.332 0c.646.049 1.288.11 1.927.184 1.1.128 1.907 1.077 1.907 2.185V19.5a2.25 2.25 0 01-2.25 2.25H6.75A2.25 2.25 0 014.5 19.5V6.257c0-1.108.806-2.057 1.907-2.185a48.208 48.208 0 011.927-.184" />
                  </svg>
                )}
              </button>
            </div>
            {message.jobs && message.jobs.length > 0 && (
              <div className="mt-2" style={{ animation: "fadeIn 0.3s ease-out" }}>
                <JobList jobs={message.jobs} />
              </div>
            )}
            {message.careerEmails && message.careerEmails.length > 0 && (
              <div className="mt-3" style={{ animation: "fadeIn 0.3s ease-out" }}>
                <div className="flex items-center gap-2 mb-2">
                  <span className="text-xs font-semibold" style={{ color: "var(--text-secondary)" }}>Career Emails</span>
                  <div style={{ flex: 1, height: 1, background: "var(--line)" }} />
                </div>
                <div className="flex flex-col gap-2">
                  {message.careerEmails.map((email, i) => (
                    <CareerEmailCard key={email.messageId || i} email={email} />
                  ))}
                </div>
              </div>
            )}
            {message.followUpQuestion && (
              <div className="mt-2.5 text-xs" style={{ display: "inline-block", background: "var(--muted)", color: "var(--accent)", padding: "4px 10px", borderRadius: 4, lineHeight: 1.4 }}>
                {message.followUpQuestion}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}