"use client";

import { RecommendedJob } from "@/lib/types";

function ScoreBar({ score }: { score: number }) {
  const pct = Math.round(score * 100);
  const color =
    pct >= 80
      ? "bg-green-500"
      : pct >= 60
        ? "bg-yellow-500"
        : "bg-gray-500";

  return (
    <div className="flex items-center gap-2">
      <div className="w-16 h-1.5 rounded-full bg-gray-700 overflow-hidden">
        <div className={`h-full rounded-full ${color}`} style={{ width: `${pct}%` }} />
      </div>
      <span className="text-xs text-gray-400">{pct}%</span>
    </div>
  );
}

export default function JobCard({ job }: { job: RecommendedJob }) {
  return (
    <div className="rounded-xl border border-gray-700/50 bg-gray-800/50 p-4 hover:border-gray-600 transition-colors">
      <div className="flex items-start gap-3">
        {job.companyLogo ? (
          <img
            src={job.companyLogo}
            alt={job.company}
            className="w-10 h-10 rounded-lg object-contain bg-white shrink-0"
          />
        ) : (
          <div className="w-10 h-10 rounded-lg bg-gray-700 flex items-center justify-center text-sm font-bold text-gray-300 shrink-0">
            {job.company?.[0] || "?"}
          </div>
        )}
        <div className="flex-1 min-w-0">
          <h3 className="text-sm font-semibold text-white truncate">
            {job.role}
          </h3>
          <p className="text-xs text-gray-400 truncate">{job.company}</p>
        </div>
      </div>

      <div className="mt-3 flex flex-wrap gap-2 text-xs">
        {job.location && (
          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md bg-gray-700/50 text-gray-300">
            <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" />
              <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" />
            </svg>
            {job.location}
          </span>
        )}
        {job.employmentType && (
          <span className="px-2 py-0.5 rounded-md bg-blue-500/10 text-blue-400">
            {job.employmentType}
          </span>
        )}
        {job.workplaceType && (
          <span className="px-2 py-0.5 rounded-md bg-purple-500/10 text-purple-400">
            {job.workplaceType}
          </span>
        )}
        {job.salary && (
          <span className="px-2 py-0.5 rounded-md bg-green-500/10 text-green-400">
            {job.salary}
          </span>
        )}
      </div>

      {job.recommendationReason && (
        <p className="mt-2 text-xs text-gray-400 line-clamp-2">
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
            className="text-xs font-medium text-blue-400 hover:text-blue-300 transition-colors"
          >
            Apply &rarr;
          </a>
        )}
      </div>
    </div>
  );
}
