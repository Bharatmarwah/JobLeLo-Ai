function ScoreBar({ score }) {
  const pct = Math.min(100, Math.round(score > 1 ? score : score * 100));
  const barColor =
    pct >= 80 ? "var(--emerald)" :
    pct >= 60 ? "var(--marigold)" :
    "var(--text-secondary)";

  return (
    <div className="flex items-center gap-2">
      <div className="w-20 h-2 rounded-full overflow-hidden" style={{ background: "var(--muted)" }}>
        <div className="h-full rounded-full" style={{ width: `${pct}%`, background: barColor }} />
      </div>
      <span className="text-xs font-semibold" style={{ color: barColor }}>{pct}% match</span>
    </div>
  );
}

export default function JobCard({ job }) {
  return (
    <div style={{ border: "1px solid var(--line)", background: "var(--card)", borderRadius: 6 }} className="p-4">
      <div className="flex items-start gap-3">
        {job.companyLogo ? (
          <img
            src={job.companyLogo}
            alt={job.company}
            className="w-10 h-10 object-contain bg-white shrink-0"
            style={{ borderRadius: 4 }}
          />
        ) : (
          <div className="w-10 h-10 flex items-center justify-center text-sm font-bold shrink-0"
               style={{ background: "var(--muted)", color: "var(--text-secondary)", borderRadius: 4 }}>
            {job.company?.[0] || "?"}
          </div>
        )}
        <div className="flex-1 min-w-0">
          <h3 className="text-sm font-semibold truncate" style={{ color: "var(--text-primary)" }}>
            {job.role}
          </h3>
          <p className="text-xs truncate" style={{ color: "var(--text-secondary)" }}>{job.company}</p>
        </div>
      </div>

      <div className="mt-3 flex flex-wrap gap-2 text-xs">
        {job.location && (
          <span className="inline-flex items-center gap-1 px-2 py-0.5" style={{ background: "var(--muted)", color: "var(--text-secondary)", borderRadius: 4 }}>
            <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" />
              <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" />
            </svg>
            {job.location}
          </span>
        )}
        {job.employmentType && (
          <span className="px-2 py-0.5" style={{ background: "var(--muted)", color: "var(--accent)", borderRadius: 4 }}>
            {job.employmentType}
          </span>
        )}
        {job.workplaceType && (
          <span className="px-2 py-0.5" style={{ background: "var(--muted)", color: "var(--ink-soft)", borderRadius: 4 }}>
            {job.workplaceType}
          </span>
        )}
        {job.salary && (
          <span className="px-2 py-0.5" style={{ background: "var(--muted)", color: "var(--emerald)", borderRadius: 4 }}>
            {job.salary}
          </span>
        )}
      </div>

      {job.recommendationReason && (
        <p className="mt-2 text-xs line-clamp-2" style={{ color: "var(--text-secondary)" }}>
          {job.recommendationReason}
        </p>
      )}

      <div className="mt-3 flex items-center justify-between">
        <ScoreBar score={job.relevanceScore ?? 0} />
        {job.applyUrl && (
          <a
            href={job.applyUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="text-xs font-medium transition-colors"
            style={{ color: "var(--accent)" }}
            onMouseEnter={(e) => { e.currentTarget.style.opacity = "0.7"; }}
            onMouseLeave={(e) => { e.currentTarget.style.opacity = "1"; }}
          >
            Apply &rarr;
          </a>
        )}
      </div>
    </div>
  );
}