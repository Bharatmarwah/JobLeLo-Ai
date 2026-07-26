package in.joblelo.JobAgentBackend.planner.mapper;

import in.joblelo.JobAgentBackend.planner.model.adzuna.AdzunaJob;
import in.joblelo.JobAgentBackend.planner.model.adzuna.AdzunaResponse;
import in.joblelo.JobAgentBackend.planner.model.JobMetadata;
import in.joblelo.JobAgentBackend.planner.model.JobProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AdzunaMapper implements JobMetadataMapper<AdzunaResponse> {

    @Override
    public List<JobMetadata> toJobMetadata(AdzunaResponse response) {

        if (response == null || response.getResults() == null) {
            return Collections.emptyList();
        }

        return response.getResults()
                .stream()
                .map(this::map)
                .toList();
    }

    private JobMetadata map(AdzunaJob job) {

        String salary = null;

        if (job.getSalary_min() != null && job.getSalary_max() != null) {
            salary = job.getSalary_min() + " - " + job.getSalary_max();
        } else if (job.getSalary_min() != null) {
            salary = String.valueOf(job.getSalary_min());
        } else if (job.getSalary_max() != null) {
            salary = String.valueOf(job.getSalary_max());
        }

        return JobMetadata.builder()
                .provider(JobProvider.ADZUNA)
                .jobId(job.getId())
                .source("Adzuna")
                .role(job.getTitle())
                .company(job.getCompany().getDisplayName())
                .location(job.getLocation().getDisplayName())
                .employmentType(job.getContract_type())
                .workplaceType(null)
                .experience(null)
                .salary(salary)
                .applyUrl(job.getRedirectUrl())
                .description(job.getDescription())
                .skills(null)
                .companyLogo(null)
                .publishedAt(null)
                .build();
    }
}