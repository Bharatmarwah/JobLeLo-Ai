import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../main";
import { getUserJobs, removeUserJob } from "../lib/api";
import Sidebar from "../components/Sidebar";
import ConfirmModal from "../components/ConfirmModal";

const PROVIDER_COLORS = {
  ADZUNA: { bg: "rgba(79,70,229,0.08)", text: "#4F46E5" },
  JOOBLE: { bg: "rgba(31,111,92,0.1)", text: "var(--emerald)" },
  REMOTIVE: { bg: "rgba(168,85,247,0.1)", text: "#A855F7" },
  GMAIL: { bg: "rgba(234,67,53,0.1)", text: "#EA4335" },
};

function timeAgo(iso) {
  const diff = Date.now() - new Date(iso).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 1) return "just now";
  if (mins < 60) return `${mins}m ago`;
  const hrs = Math.floor(mins / 60);
  if (hrs < 24) return `${hrs}h ago`;
  const days = Math.floor(hrs / 24);
  if (days < 30) return `${days}d ago`;
  const months = Math.floor(days / 30);
  return `${months}mo ago`;
}

export default function DashboardPage() {
  const { isLoggedIn, loading } = useAuth();
  const navigate = useNavigate();
  const [jobs, setJobs] = useState([]);
  const [loadingJobs, setLoadingJobs] = useState(true);
  const [sort, setSort] = useState("recent");
  const [deleteTarget, setDeleteTarget] = useState(null);

  useEffect(() => {
    console.log("[Dashboard] Page rendered, isLoggedIn:", isLoggedIn, "loading:", loading);
    if (!loading && !isLoggedIn) {
      console.log("[Dashboard] Redirecting to login, user not authenticated");
      navigate("/");
    }
  }, [loading, isLoggedIn, navigate]);

  useEffect(() => {
    if (isLoggedIn) {
      console.log("[Dashboard] Fetching jobs, sort:", sort);
      setLoadingJobs(true);
      getUserJobs(sort)
        .then((data) => {
          setJobs(data);
          console.log("[Dashboard] Jobs loaded:", data.length);
        })
        .catch((err) => {
          console.log("[Dashboard] Error loading jobs:", err.message);
        })
        .finally(() => setLoadingJobs(false));
    }
  }, [isLoggedIn, sort]);

  const handleSortChange = (newSort) => {
    console.log("[Dashboard] Sort changed to:", newSort);
    setSort(newSort);
  };

  const handleStartSearch = () => {
    console.log("[Dashboard] Navigate to /chat");
    navigate("/chat");
  };

  const handleDeleteJob = (id) => {
    setDeleteTarget(id);
  };

  const confirmDelete = async () => {
    if (deleteTarget == null) return;
    try {
      await removeUserJob(deleteTarget);
      setJobs((prev) => prev.filter((j) => j.id !== deleteTarget));
      console.log("[Dashboard] Job removed from UI:", deleteTarget);
    } catch (err) {
      console.log("[Dashboard] Failed to delete job:", err.message);
    } finally {
      setDeleteTarget(null);
    }
  };

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center" style={{ background: "var(--page)" }}>
        <div className="animate-pulse" style={{ color: "var(--text-secondary)" }}>Loading...</div>
      </div>
    );
  }

  if (!isLoggedIn) return null;

  return (
    <div className="flex h-screen overflow-hidden" style={{ background: "var(--page)" }}>
      <Sidebar />

      <main className="flex-1 flex flex-col min-w-0">
        <header className="h-14 flex items-center justify-between px-6 shrink-0" style={{ background: "var(--page)", borderBottom: "1px dashed var(--line)" }}>
          <div className="flex items-center gap-3">
            <h1 style={{ fontFamily: "'IBM Plex Mono', monospace", fontSize: 13, letterSpacing: "0.4px", color: "var(--text-secondary)" }}>
              MY JOBS
            </h1>
            <span className="text-xs" style={{ color: "var(--text-secondary)" }}>{jobs.length} saved</span>
          </div>

          <div className="flex p-0.5" style={{ background: "var(--muted)", borderRadius: 4 }}>
            <button
              onClick={() => handleSortChange("recent")}
              className="px-3 py-1 text-xs font-medium transition-colors"
              style={{
                borderRadius: 3,
                background: sort === "recent" ? "var(--card)" : "transparent",
                color: sort === "recent" ? "var(--text-primary)" : "var(--text-secondary)",
              }}
            >
              Most Recent
            </button>
            <button
              onClick={() => handleSortChange("relevance")}
              className="px-3 py-1 text-xs font-medium transition-colors"
              style={{
                borderRadius: 3,
                background: sort === "relevance" ? "var(--card)" : "transparent",
                color: sort === "relevance" ? "var(--text-primary)" : "var(--text-secondary)",
              }}
            >
              Best Match
            </button>
          </div>
        </header>

        <div className="flex-1 overflow-y-auto">
          <div className="max-w-5xl mx-auto px-4 py-6">
            {loadingJobs ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {[1, 2, 3, 4, 5, 6].map((i) => (
                  <div
                    key={i}
                    className="h-40 animate-pulse"
                    style={{ background: "var(--muted)", borderRadius: 6 }}
                  />
                ))}
              </div>
            ) : jobs.length === 0 ? (
              <div className="text-center py-20">
                <svg
                  className="w-12 h-12 mx-auto mb-4"
                  style={{ color: "var(--text-secondary)" }}
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  strokeWidth={1}
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M20.25 14.15v4.25c0 1.094-.787 2.036-1.872 2.18-2.087.277-4.216.42-6.378.42s-4.291-.143-6.378-.42c-1.085-.144-1.872-1.086-1.872-2.18v-4.25m16.5 0a2.18 2.18 0 00.75-1.661V8.706c0-1.081-.768-2.015-1.837-2.175a48.114 48.114 0 00-3.413-.387m4.5 8.006c-.194.165-.42.295-.673.38A23.978 23.978 0 0112 15.75c-2.648 0-5.195-.429-7.577-1.22a2.016 2.016 0 01-.673-.38m0 0A2.18 2.18 0 013 12.489V8.706c0-1.081.768-2.015 1.837-2.175a48.111 48.111 0 013.413-.387m7.5 0V5.25A2.25 2.25 0 0013.5 3h-3a2.25 2.25 0 00-2.25 2.25v.894m7.5 0a48.667 48.667 0 00-7.5 0"
                  />
                </svg>
                <h2 className="text-lg font-medium mb-1" style={{ color: "var(--text-primary)" }}>
                  No saved jobs yet
                </h2>
                <p className="text-sm" style={{ color: "var(--text-secondary)" }}>
                  Start a chat to find and save jobs
                </p>
                <button
                  onClick={handleStartSearch}
                  className="mt-4 px-4 py-2 text-sm font-medium transition-colors"
                  style={{ background: "var(--marigold)", color: "var(--ink)", borderRadius: 4 }}
                  onMouseEnter={(e) => { e.currentTarget.style.opacity = "0.9"; }}
                  onMouseLeave={(e) => { e.currentTarget.style.opacity = "1"; }}
                >
                  Start Searching
                </button>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {jobs.map((job) => (
                  <div
                    key={job.id}
                    className="border p-4 transition-colors flex flex-col"
                    style={{ borderColor: "var(--line)", background: "var(--card)", borderRadius: 6 }}
                    onMouseEnter={(e) => { e.currentTarget.style.borderColor = "var(--line-strong)"; }}
                    onMouseLeave={(e) => { e.currentTarget.style.borderColor = "var(--line)"; }}
                  >
                    <div className="flex items-start justify-between mb-2">
                      <div className="flex-1 min-w-0">
                        <h3 className="text-sm font-semibold truncate" style={{ color: "var(--text-primary)" }}>
                          {job.role}
                        </h3>
                        <p className="text-xs truncate" style={{ color: "var(--text-secondary)" }}>
                          {job.company}
                        </p>
                      </div>
                      <span
                        className="shrink-0 text-[10px] px-1.5 py-0.5 font-medium"
                        style={{
                          borderRadius: 3,
                          ...(PROVIDER_COLORS[job.provider] || { background: "var(--muted)", color: "var(--text-secondary)" })
                        }}
                      >
                        {job.provider}
                      </span>
                    </div>

                    <div className="flex flex-wrap gap-1.5 text-xs mb-2">
                      {job.location && (
                        <span className="px-2 py-0.5" style={{ background: "var(--muted)", color: "var(--text-secondary)", borderRadius: 4 }}>
                          {job.location}
                        </span>
                      )}
                      {job.employmentType && (
                        <span className="px-2 py-0.5" style={{ background: "var(--accent-soft)", color: "var(--accent)", borderRadius: 4 }}>
                          {job.employmentType}
                        </span>
                      )}
                      {job.salary && (
                        <span className="px-2 py-0.5" style={{ background: "rgba(31,111,92,0.1)", color: "var(--emerald)", borderRadius: 4 }}>
                          {job.salary}
                        </span>
                      )}
                    </div>

                    {job.rankingReason && (
                      <p className="text-xs line-clamp-2 mb-3 flex-1" style={{ color: "var(--text-secondary)" }}>
                        {job.rankingReason}
                      </p>
                    )}

                    <div className="flex items-center justify-between mt-auto pt-2" style={{ borderTop: "1px dashed var(--line)" }}>
                      {sort === "relevance" && job.relevanceScore != null ? (
                        <div className="flex items-center gap-2">
                          <div className="w-12 h-1 rounded-full overflow-hidden" style={{ background: "var(--muted)" }}>
                            <div
                              className="h-full rounded-full"
                              style={{
                                background: "var(--marigold)",
                                width: `${Math.min(100, Math.round(job.relevanceScore > 1 ? job.relevanceScore : job.relevanceScore * 100))}%`,
                              }}
                            />
                          </div>
                          <span className="text-[10px]" style={{ color: "var(--text-secondary)" }}>
                            {Math.min(100, Math.round(job.relevanceScore > 1 ? job.relevanceScore : job.relevanceScore * 100))}%
                          </span>
                        </div>
                      ) : job.createdAt ? (
                        <span className="text-[10px]" style={{ color: "var(--text-secondary)" }}>
                          Saved {timeAgo(job.createdAt)}
                        </span>
                      ) : null}
                      <div className="flex items-center gap-2">
                        <button
                          onClick={(e) => { e.preventDefault(); handleDeleteJob(job.id); }}
                          className="transition-colors"
                          style={{ color: "var(--text-secondary)" }}
                          onMouseEnter={(e) => { e.currentTarget.style.color = "var(--vermillion)"; }}
                          onMouseLeave={(e) => { e.currentTarget.style.color = "var(--text-secondary)"; }}
                          title="Remove job"
                        >
                          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
                          </svg>
                        </button>
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
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <ConfirmModal
          open={deleteTarget != null}
          title="Remove job"
          message="Are you sure you want to remove this saved job?"
          onConfirm={confirmDelete}
          onCancel={() => setDeleteTarget(null)}
        />
      </main>
    </div>
  );
}
