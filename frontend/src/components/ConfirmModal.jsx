export default function ConfirmModal({ open, title, message, onConfirm, onCancel }) {
  if (!open) return null;

  return (
    <>
      <div className="fixed inset-0 bg-black/50 z-40 animate-[fadeIn_0.15s_ease-out]" onClick={onCancel} />
      <div className="fixed inset-0 flex items-center justify-center z-50 pointer-events-none">
        <div className="w-80 pointer-events-auto animate-[fadeIn_0.15s_ease-out] p-5 border"
             style={{ background: "var(--card)", borderColor: "var(--line)", borderRadius: 6 }}>
          <div className="flex items-center gap-3 mb-3">
            <div className="w-8 h-8 rounded-full flex items-center justify-center shrink-0"
                 style={{ background: "var(--vermillion)", opacity: 0.1 }}>
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}
                   style={{ color: "var(--vermillion)" }}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z" />
              </svg>
            </div>
            <h3 className="text-sm font-semibold" style={{ color: "var(--text-primary)" }}>{title || "Confirm"}</h3>
          </div>
          <p className="text-sm" style={{ color: "var(--text-secondary)" }}>{message}</p>
          <div className="flex gap-2 mt-4">
            <button
              onClick={onCancel}
              className="flex-1 text-sm font-medium px-3 py-2 transition-colors"
              style={{ border: "1px solid var(--line)", color: "var(--text-secondary)", borderRadius: 4 }}
              onMouseEnter={(e) => { e.currentTarget.style.background = "var(--muted)"; }}
              onMouseLeave={(e) => { e.currentTarget.style.background = "transparent"; }}
            >
              Cancel
            </button>
            <button
              onClick={onConfirm}
              className="flex-1 text-sm font-medium px-3 py-2 transition-colors"
              style={{ background: "var(--vermillion)", color: "var(--paper)", borderRadius: 4 }}
              onMouseEnter={(e) => { e.currentTarget.style.opacity = "0.9"; }}
              onMouseLeave={(e) => { e.currentTarget.style.opacity = "1"; }}
            >
              Delete
            </button>
          </div>
        </div>
      </div>
    </>
  );
}