import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth, useChat } from "../main";
import Sidebar from "../components/Sidebar";
import ChatMessageBubble from "../components/ChatMessage";
import ChatInput from "../components/ChatInput";

const PHRASES = [
  "Analyzing your request",
  "Searching across sources",
  "Scanning your inbox",
  "Generating response",
];

function LoadingIndicator() {
  const [phase, setPhase] = useState(0);
  const [visible, setVisible] = useState(false);

  useEffect(() => { requestAnimationFrame(() => setVisible(true)); }, []);

  useEffect(() => {
    const timers = [
      setTimeout(() => setPhase(1), 3000),
      setTimeout(() => setPhase(2), 8000),
      setTimeout(() => setPhase(3), 13000),
    ];
    return () => timers.forEach(clearTimeout);
  }, []);

  return (
    <div
      className="flex justify-start mb-4"
      style={{
        opacity: visible ? 1 : 0,
        transform: visible ? "translateY(0)" : "translateY(8px)",
        transition: "opacity 0.4s ease-out, transform 0.4s ease-out",
      }}
    >
      <div className="max-w-[80%]">
        <div className="flex items-center gap-2 mb-2">
          <div className="relative">
            <div className="w-6 h-6 rounded-full flex items-center justify-center relative z-10" style={{ background: "var(--muted)" }}>
              <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2} style={{ color: "var(--ink-soft)" }}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09z" />
              </svg>
            </div>
            <span
              className="absolute inset-0 rounded-full animate-ping opacity-30"
              style={{ background: "var(--accent)", animationDuration: "2s" }}
            />
          </div>
          <span className="text-xs font-medium" style={{ color: "var(--text-secondary)" }}>AI</span>
        </div>
        <div
          className="px-4 py-3 relative"
          style={{
            background: "var(--card)",
            border: "1px solid var(--line)",
            borderRadius: 6,
          }}
        >
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-1.5 h-4">
              {[0, 1, 2].map((i) => (
                <span
                  key={i}
                  className="w-1.5 h-1.5 rounded-full"
                  style={{
                    background: "var(--accent)",
                    opacity: phase >= i ? 1 : 0.2,
                    transform: phase >= i ? "scale(1)" : "scale(0.5)",
                    transition: "all 0.4s ease-out",
                    transitionDelay: `${i * 100}ms`,
                  }}
                />
              ))}
            </div>
            <div className="flex items-baseline gap-0.5 overflow-hidden" style={{ height: 22 }}>
              <span
                className="whitespace-nowrap transition-all duration-500"
                style={{
                  fontSize: phase === 0 ? 16 : 14,
                  color: "var(--text-primary)",
                  fontWeight: phase === 0 ? 550 : 400,
                  transform: "translateY(0)",
                  opacity: 1,
                }}
              >
                {PHRASES[phase]}
              </span>
              <span
                className="inline-block"
                style={{
                  width: 2,
                  height: phase === 0 ? 17 : 15,
                  background: "var(--accent)",
                  animation: "blink 0.9s step-end infinite",
                  alignSelf: "center",
                }}
              />
            </div>
          </div>
          <div
            className="absolute bottom-0 left-0 h-[2px] rounded-full"
            style={{
              width: `${((phase + 1) / PHRASES.length) * 100}%`,
              background: "var(--accent)",
              opacity: 0.35,
              transition: "width 0.8s ease-out",
            }}
          />
        </div>
      </div>
    </div>
  );
}

export default function ChatPage() {
  const { isLoggedIn, loading } = useAuth();
  const { messages, sending, sendMessage, newChat } = useChat();
  const navigate = useNavigate();
  const bottomRef = useRef(null);
  const scrollRef = useRef(null);
  const [showScrollBtn, setShowScrollBtn] = useState(false);

  useEffect(() => {
    if (!loading && !isLoggedIn) {
      navigate("/");
    }
  }, [loading, isLoggedIn, navigate]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, sending]);

  useEffect(() => {
    const el = scrollRef.current;
    if (!el) return;
    const handleScroll = () => {
      const dist = el.scrollHeight - el.scrollTop - el.clientHeight;
      setShowScrollBtn(dist > 200);
    };
    el.addEventListener("scroll", handleScroll);
    return () => el.removeEventListener("scroll", handleScroll);
  }, []);

  const scrollToBottom = () => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center" style={{ background: "var(--page)", color: "var(--text-secondary)" }}>
        <div className="animate-pulse">Loading...</div>
      </div>
    );
  }

  if (!isLoggedIn) return null;

  return (
    <div className="flex h-screen overflow-hidden" style={{ background: "var(--page)" }}>
      <Sidebar />

      <main className="flex-1 flex flex-col min-w-0">
        <header
          className="h-14 flex items-center justify-between px-6 shrink-0"
          style={{
            background: "var(--page)",
            borderBottom: "1px dashed var(--line)",
            color: "var(--text-primary)",
          }}
        >
          <h1 style={{ fontFamily: "'IBM Plex Mono', monospace", fontSize: 13, letterSpacing: "0.4px", color: "var(--text-secondary)" }}>
            {messages.length === 0 ? "NEW CHAT" : "CHAT"}
          </h1>
          <button
            onClick={newChat}
            className="flex items-center gap-1.5 text-xs px-3 py-1.5"
            style={{
              fontFamily: "'IBM Plex Mono', monospace",
              fontSize: 11.5,
              letterSpacing: "0.4px",
              border: "1px solid var(--line)",
              color: "var(--text-secondary)",
              background: "transparent",
              borderRadius: 4,
            }}
            onMouseEnter={(e) => { e.currentTarget.style.borderColor = "var(--line-strong)"; e.currentTarget.style.color = "var(--text-primary)"; }}
            onMouseLeave={(e) => { e.currentTarget.style.borderColor = "var(--line)"; e.currentTarget.style.color = "var(--text-secondary)"; }}
          >
            <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
            </svg>
            NEW
          </button>
        </header>

        <div ref={scrollRef} className="flex-1 overflow-y-auto scroll-smooth">
          <div className="max-w-3xl mx-auto px-4 py-8">
            {messages.length === 0 && (
              <div className="flex flex-col items-center justify-center" style={{ minHeight: "60vh" }}>
                <div
                  className="w-12 h-12 rounded-full flex items-center justify-center mb-5"
                  style={{ background: "var(--muted)" }}
                >
                  <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5} style={{ color: "var(--ink-soft)" }}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09z" />
                  </svg>
                </div>
                <h2 className="text-xl font-semibold mb-1 text-center" style={{ color: "var(--text-primary)" }}>
                  What kind of job are you looking for?
                </h2>
                <p className="text-sm text-center max-w-md" style={{ color: "var(--text-secondary)" }}>
                  AI searches across sources and your inbox to find the best matches for you.
                </p>
                <div className="flex flex-wrap justify-center gap-2 mt-8 max-w-lg">
                  {[
                    "Backend developer in Delhi",
                    "Remote React jobs",
                    "Full-stack roles with 3 years exp",
                    "DevOps engineer in Bangalore",
                  ].map((suggestion, i) => (
                    <button
                      key={suggestion}
                      onClick={() => sendMessage(suggestion)}
                      className="text-xs px-3.5 py-2 border transition-all opacity-0"
                      style={{
                        borderColor: "var(--line)",
                        color: "var(--text-secondary)",
                        background: "var(--card)",
                        borderRadius: 4,
                        animation: `fadeIn 0.5s ease-out ${i * 100 + 300}ms forwards`,
                      }}
                      onMouseEnter={(e) => { e.currentTarget.style.borderColor = "var(--accent)"; e.currentTarget.style.color = "var(--accent)"; }}
                      onMouseLeave={(e) => { e.currentTarget.style.borderColor = "var(--line)"; e.currentTarget.style.color = "var(--text-secondary)"; }}
                    >
                      {suggestion}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {messages.map((msg) => (
              <ChatMessageBubble key={msg.id} message={msg} />
            ))}

            {sending && <LoadingIndicator />}

            <div ref={bottomRef} />
          </div>
        </div>

        {showScrollBtn && (
          <button
            onClick={scrollToBottom}
            className="absolute bottom-24 right-8 w-8 h-8 flex items-center justify-center z-10 shadow-sm border transition-all"
            style={{ background: "var(--card)", borderColor: "var(--line)", color: "var(--text-secondary)", borderRadius: 4 }}
            onMouseEnter={(e) => { e.currentTarget.style.color = "var(--text-primary)"; e.currentTarget.style.borderColor = "var(--line-strong)"; }}
            onMouseLeave={(e) => { e.currentTarget.style.color = "var(--text-secondary)"; e.currentTarget.style.borderColor = "var(--line)"; }}
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5" />
            </svg>
          </button>
        )}

        <ChatInput onSend={sendMessage} disabled={sending} />
      </main>
    </div>
  );
}