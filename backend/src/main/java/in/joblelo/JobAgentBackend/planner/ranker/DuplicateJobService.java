package in.joblelo.JobAgentBackend.planner.ranker;

import in.joblelo.JobAgentBackend.planner.model.PlannerContext;
import in.joblelo.JobAgentBackend.planner.model.RankedJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class DuplicateJobService {

    public List<RankedJob> removeDuplicates(
            List<RankedJob> rankedJobs,
            PlannerContext context
    ) {

        if (rankedJobs == null || rankedJobs.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, RankedJob> uniqueJobs = new LinkedHashMap<>();

        for (RankedJob rankedJob : rankedJobs) {

            String key = buildKey(rankedJob);

            RankedJob existing = uniqueJobs.get(key);

            if (existing == null ||
                    rankedJob.getRelevanceScore() > existing.getRelevanceScore()) {

                uniqueJobs.put(key, rankedJob);
            }
        }

        List<RankedJob> deduplicated =
                new ArrayList<>(uniqueJobs.values());

        deduplicated.sort(
                Comparator.comparingDouble(RankedJob::getRelevanceScore)
                        .reversed()
        );

        log.info(
                "Removed {} duplicate jobs.",
                rankedJobs.size() - deduplicated.size()
        );

        return deduplicated;
    }

    private String buildKey(RankedJob rankedJob) {

        if (rankedJob == null || rankedJob.getJob() == null) {
            return UUID.randomUUID().toString();
        }

        var job = rankedJob.getJob();

        // 1. Apply URL (best unique identifier)
        if (job.getApplyUrl() != null &&
                !job.getApplyUrl().isBlank()) {

            return normalize(job.getApplyUrl());
        }

        // 2. Provider + Provider Job ID
        if (job.getProvider() != null &&
                job.getJobId() != null) {

            return normalize(
                    job.getProvider() + "|" + job.getJobId()
            );
        }

        // 3. Fallback
        return normalize(
                job.getCompany() + "|" +
                job.getRole() + "|" +
                job.getLocation()
        );
    }

    private String normalize(String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", " ");
    }

}