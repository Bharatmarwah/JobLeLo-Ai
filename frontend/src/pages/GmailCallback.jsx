import { useEffect, useRef, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { gmailCallback } from "../lib/api";

export default function GmailCallback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState("Processing...");
  const calledRef = useRef(false);

  useEffect(() => {
    if (calledRef.current) return;
    calledRef.current = true;

    const code = searchParams.get("code");
    if (!code) {
      setStatus("No authorization code received.");
      return;
    }

    gmailCallback(code)
      .then(() => {
        setStatus("Gmail connected!");
        if (window.opener) {
          window.opener.postMessage("gmail_connected", window.location.origin);
          setTimeout(() => window.close(), 500);
        } else {
          setTimeout(() => navigate("/chat"), 1000);
        }
      })
      .catch((err) => {
        setStatus("Connection failed. Please try again.");
      });
  }, [searchParams, navigate]);

  return (
    <div className="flex h-screen items-center justify-center" style={{ background: "var(--page)" }}>
      <div className="text-center">
        <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl mb-4" style={{ background: "var(--muted)" }}>
          <svg className="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5} style={{ color: "var(--ink-soft)" }}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09z" />
          </svg>
        </div>
        <p style={{ color: "var(--text-primary)" }}>{status}</p>
      </div>
    </div>
  );
}