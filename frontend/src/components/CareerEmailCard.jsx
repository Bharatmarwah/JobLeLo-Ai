const TYPE_LABELS = {
  RECRUITER_MESSAGE: "Recruiter Message",
  INTERVIEW: "Interview",
  ASSESSMENT: "Assessment",
  OFFER: "Offer",
  REJECTION: "Rejection",
  APPLICATION_RECEIVED: "Application Received",
  APPLICATION_UPDATE: "Application Update",
  FOLLOW_UP: "Follow-up",
  OTHER: "Update",
};

const TYPE_COLORS = {
  OFFER: { bg: "rgba(31,111,92,0.12)", text: "var(--emerald)" },
  INTERVIEW: { bg: "rgba(79,70,229,0.08)", text: "#4F46E5" },
  REJECTION: { bg: "rgba(225,79,61,0.08)", text: "var(--vermillion)" },
  ASSESSMENT: { bg: "rgba(244,166,35,0.12)", text: "var(--marigold)" },
};

export default function CareerEmailCard({ email }) {
  const typeStyle = TYPE_COLORS[email.type] || { bg: "var(--muted)", text: "var(--text-secondary)" };

  return (
    <div style={{ border: "1px dashed var(--line)", background: "var(--muted)", borderRadius: 6 }} className="p-3">
      <div className="flex items-center gap-2 mb-2">
        <span className="text-[10px] font-semibold uppercase tracking-wider px-1.5 py-0.5" style={{ background: "var(--card)", color: "var(--text-secondary)", borderRadius: 3 }}>
          Email
        </span>
        <span className="text-[11px] font-medium px-1.5 py-0.5" style={{ background: typeStyle.bg, color: typeStyle.text, borderRadius: 3 }}>
          {TYPE_LABELS[email.type] || email.type}
        </span>
        {email.priority === "HIGH" && (
          <span className="text-[10px]" style={{ color: "var(--vermillion)" }}>● High</span>
        )}
      </div>

      {email.subject && (
        <p className="text-sm font-medium truncate" style={{ color: "var(--text-primary)" }}>{email.subject}</p>
      )}
      {email.summary && (
        <p className="text-xs mt-1" style={{ color: "var(--text-secondary)", lineHeight: 1.5 }}>{email.summary}</p>
      )}

      <div className="flex items-center gap-2 mt-2 text-[11px]" style={{ color: "var(--text-secondary)" }}>
        {email.sender && <span>{email.sender}</span>}
        {email.company && <span>· {email.company}</span>}
        {email.role && <span>· {email.role}</span>}
      </div>

      {email.actionRequired && (
        <div className="mt-2 text-xs" style={{ color: "var(--accent)", lineHeight: 1.4 }}>
          Action: {email.actionRequired}
        </div>
      )}
    </div>
  );
}