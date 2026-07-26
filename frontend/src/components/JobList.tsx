"use client";

import { RecommendedJob } from "@/lib/types";
import JobCard from "./JobCard";

export default function JobList({ jobs }: { jobs: RecommendedJob[] }) {
  if (!jobs || jobs.length === 0) return null;

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-3">
      {jobs.map((job, i) => (
        <JobCard key={`${job.company}-${job.role}-${i}`} job={job} />
      ))}
    </div>
  );
}
