import { useState, useRef, useEffect } from "react";
import { getGmailStatus, getGmailAuthUrl } from "../lib/api";

export default function ChatInput({ onSend, disabled }) {
  const [input, setInput] = useState("");
  const [gmailConnected, setGmailConnected] = useState(false);
  const [showTools, setShowTools] = useState(false);
  const [connecting, setConnecting] = useState(false);
  const textareaRef = useRef(null);
  const modalRef = useRef(null);

  const checkGmail = async () => {
    try {
      const res = await getGmailStatus();
      setGmailConnected(res.connected);
    } catch (_) {}
  };

  useEffect(() => {
    checkGmail();
  }, []);

  useEffect(() => {
    const handler = (event) => {
      if (event.data === "gmail_connected") {
        checkGmail();
      }
    };
    window.addEventListener("message", handler);
    return () => window.removeEventListener("message", handler);
  }, []);

  useEffect(() => {
    textareaRef.current?.focus();
  }, [disabled]);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (modalRef.current && !modalRef.current.contains(e.target)) {
        setShowTools(false);
      }
    };
    if (showTools) {
      document.addEventListener("mousedown", handleClickOutside);
      return () => document.removeEventListener("mousedown", handleClickOutside);
    }
  }, [showTools]);

  const handleSend = () => {
    const trimmed = input.trim();
    if (!trimmed || disabled) return;
    onSend(trimmed);
    setInput("");
    if (textareaRef.current) {
      textareaRef.current.style.height = "auto";
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      if (e.shiftKey) return;
      e.preventDefault();
      handleSend();
    }
  };

  const handleInput = () => {
    const el = textareaRef.current;
    if (el) {
      el.style.height = "auto";
      el.style.height = Math.min(el.scrollHeight, 160) + "px";
    }
  };

  const handleGmailConnect = async () => {
    setConnecting(true);
    try {
      const authUrl = await getGmailAuthUrl();
      window.open(authUrl, "gmail_connect", "width=600,height=700");
      setShowTools(false);
    } catch (_) {}
    setConnecting(false);
  };

  return (
    <div className="px-4 py-3" style={{ background: "var(--page)", borderTop: "1px dashed var(--line)" }}>
      <div className="max-w-3xl mx-auto">
        <div
          className="flex items-center gap-2 px-3 py-2.5 border transition-all"
          style={{
            background: "var(--card)",
            borderColor: "var(--line)",
            borderRadius: 6,
          }}
          onFocusCapture={() => {}}
        >
          <div className="relative">
            <button
              onClick={() => setShowTools(!showTools)}
              className="shrink-0 w-8 h-8 flex items-center justify-center transition-colors"
              style={{ borderRadius: 4, color: "var(--text-secondary)" }}
              onMouseEnter={(e) => { e.currentTarget.style.background = "var(--muted)"; }}
              onMouseLeave={(e) => { e.currentTarget.style.background = "transparent"; }}
              title="Add tools"
            >
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
              </svg>
            </button>

            {showTools && (
              <>
                <div className="fixed inset-0 z-30" onClick={() => setShowTools(false)} />
                  <div
                    ref={modalRef}
                    className="absolute bottom-full left-0 mb-2 w-80 border shadow-xl z-40 overflow-hidden"
                    style={{ background: "var(--card)", borderColor: "var(--line)", borderRadius: 6 }}
                  >
                    <div className="px-4 py-3" style={{ borderBottom: "1px dashed var(--line)" }}>
                      <h3 className="text-sm font-semibold" style={{ color: "var(--text-primary)" }}>Add tools</h3>
                      <p className="text-xs mt-0.5" style={{ color: "var(--text-secondary)" }}>Connect services to search for jobs</p>
                    </div>
                    <div className="p-3">
                      <div
                        className="flex items-center gap-3 p-3 border transition-all"
                        style={{ borderColor: "var(--line)", borderRadius: 6 }}
                        onMouseEnter={(e) => { e.currentTarget.style.background = "var(--muted)"; }}
                        onMouseLeave={(e) => { e.currentTarget.style.background = "transparent"; }}
                      >
                        <div
                          className="w-10 h-10 flex items-center justify-center shrink-0"
                          style={{ borderRadius: 4, background: "rgba(234,67,53,0.1)" }}
                      >
                        <svg viewBox="0 0 48 48" className="w-6 h-6">
                          <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"/>
                          <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"/>
                          <path fill="#FBBC05" d="M10.54 28.59A14.5 14.5 0 0 1 9.5 24c0-1.59.28-3.14.76-4.59l-7.98-6.19A23.99 23.99 0 0 0 0 24c0 3.77.87 7.35 2.56 10.56l7.98-5.97z"/>
                          <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 5.97C6.51 42.62 14.62 48 24 48z"/>
                        </svg>
                      </div>
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2">
                          <span className="text-sm font-medium" style={{ color: "var(--text-primary)" }}>Gmail</span>
                          {gmailConnected && (
                            <span className="text-[10px] px-1.5 py-0.5 font-medium" style={{ background: "rgba(31,111,92,0.1)", color: "var(--emerald)", borderRadius: 4 }}>Connected</span>
                          )}
                        </div>
                        <p className="text-xs mt-0.5" style={{ color: "var(--text-secondary)" }}>Search for job-related emails in your inbox</p>
                      </div>
                      <button
                        onClick={handleGmailConnect}
                        disabled={connecting || gmailConnected}
                        className="shrink-0 text-xs font-medium px-3 py-1.5 transition-colors"
                        style={{
                          borderRadius: 4,
                          background: gmailConnected ? "rgba(31,111,92,0.1)" : "var(--marigold)",
                          color: gmailConnected ? "var(--emerald)" : "var(--ink)",
                          cursor: gmailConnected || connecting ? "default" : "pointer",
                        }}
                      >
                        {connecting ? "Connecting..." : gmailConnected ? "Connected" : "Connect"}
                      </button>
                    </div>
                    {!gmailConnected && (
                      <div className="mt-2 px-3 py-2 text-xs" style={{ color: "var(--text-secondary)", background: "var(--muted)", borderRadius: 4, lineHeight: 1.6 }}>
                        Why connect Gmail? It lets the AI scan job-related emails (application confirmations, recruiter messages) from your inbox to recommend better matches. Click <strong style={{ color: "var(--accent)" }}>Connect</strong> above — a Google pop-up will ask you to grant read-only email access.
                      </div>
                    )}
                  </div>
                </div>
              </>
            )}
          </div>

          <div className="flex-1 relative">
            <textarea
              ref={textareaRef}
              name="chat-input"
              value={input}
              onChange={(e) => {
                setInput(e.target.value);
                handleInput();
              }}
              onKeyDown={handleKeyDown}
              placeholder={disabled ? "AI is thinking..." : "Describe your ideal job..."}
              rows={1}
              disabled={disabled}
              className="w-full bg-transparent outline-none max-h-40 leading-7 resize-none"
              style={{ fontSize: 16, color: "var(--text-primary)" }}
            />
            {input.length > 500 && (
              <div className="absolute -bottom-4 right-0 text-[10px]" style={{ color: "var(--vermillion)" }}>
                {input.length}/2000
              </div>
            )}
          </div>

          <button
            onClick={handleSend}
            disabled={disabled || !input.trim()}
            className="shrink-0 w-8 h-8 flex items-center justify-center transition-all active:scale-95"
            style={{
              borderRadius: 4,
              background: "var(--marigold)",
              color: "var(--ink)",
              opacity: disabled || !input.trim() ? 0.3 : 1,
              cursor: disabled || !input.trim() ? "not-allowed" : "pointer",
            }}
            onMouseEnter={(e) => {
              if (!disabled && input.trim()) e.currentTarget.style.opacity = "0.85";
            }}
            onMouseLeave={(e) => {
              if (!disabled && input.trim()) e.currentTarget.style.opacity = "1";
            }}
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M6 12L3.269 3.126A59.768 59.768 0 0121.485 12 59.77 59.77 0 013.27 20.876L5.999 12zm0 0h7.5" />
            </svg>
          </button>
        </div>
        <p className="text-[10px] text-center mt-1.5" style={{ color: "var(--text-secondary)" }}>
          <span style={{ fontFamily: "'IBM Plex Mono', monospace", fontSize: 10, letterSpacing: "0.3px" }}>
            AI searches across sources &bull; Enter to send, Shift+Enter for new line
          </span>
        </p>
      </div>
    </div>
  );
}