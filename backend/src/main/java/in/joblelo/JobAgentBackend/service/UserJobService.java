package in.joblelo.JobAgentBackend.service;

import in.joblelo.JobAgentBackend.entity.UserJob;
import in.joblelo.JobAgentBackend.planner.model.RankedJob;
import in.joblelo.JobAgentBackend.repository.UserJobRepo;
import in.joblelo.JobAgentBackend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserJobService {

    private static final double MIN_RELEVANCE_SCORE = 70.0;

    private final UserJobRepo userJobRepo;
    private final SecurityUtils securityUtils;

    public void save(List<RankedJob> rankedJobs) {

        if (rankedJobs == null || rankedJobs.isEmpty()) {
            return;
        }

        String userId = securityUtils
                .getCurrentUserId();

        List<UserJob> existingJobs = userJobRepo.findByUserIdOrderByCreatedAtDesc(userId);
        Set<String> existingKeys = new HashSet<>();
        for (UserJob existing : existingJobs) {
            existingKeys.add(existing.getProvider() + "|" + existing.getProviderJobId());
        }

        for (RankedJob rankedJob : rankedJobs) {

            if (rankedJob.getRelevanceScore() < MIN_RELEVANCE_SCORE) {
                continue;
            }

            var job = rankedJob.getJob();
            String jobId = job.getJobId() != null ? job.getJobId() : "gmail-" + UUID.randomUUID().toString();
            String key = job.getProvider() + "|" + jobId;
            if (existingKeys.contains(key)) {
                continue;
            }

            userJobRepo.save(toEntity(userId, rankedJob));
        }
    }

    private UserJob toEntity(
            String userId,
            RankedJob rankedJob
    ) {

        var job = rankedJob.getJob();

        UserJob entity = new UserJob();

        entity.setUserId(userId);

        entity.setProvider(job.getProvider());
        entity.setProviderJobId(
                job.getJobId() != null ? job.getJobId() : "gmail-" + UUID.randomUUID().toString()
        );

        entity.setRole(job.getRole());
        entity.setCompany(job.getCompany());
        entity.setLocation(job.getLocation());
        entity.setEmploymentType(job.getEmploymentType());
        entity.setExperience(parseExperience(job.getExperience()));
        entity.setSalary(job.getSalary());

        entity.setApplyUrl(job.getApplyUrl());
        entity.setDescription(job.getDescription());

        entity.setRelevanceScore(rankedJob.getRelevanceScore());
        entity.setRankingReason(rankedJob.getRankingReason());

        return entity;
    }

    private Integer parseExperience(String exp) {
        if (exp == null || exp.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(exp.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}