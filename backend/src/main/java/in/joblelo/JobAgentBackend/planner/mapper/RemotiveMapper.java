package in.joblelo.JobAgentBackend.planner.mapper;

import in.joblelo.JobAgentBackend.planner.model.*;
import in.joblelo.JobAgentBackend.planner.model.remotive.RemotiveJob;
import in.joblelo.JobAgentBackend.planner.model.remotive.RemotiveResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RemotiveMapper implements JobMetadataMapper<RemotiveResponse> {

    @Override
    public List<JobMetadata> toJobMetadata(RemotiveResponse response) {

        if (response == null || response.getJobs() == null) {
            return Collections.emptyList();
        }

        return response.getJobs()
                .stream()
                .map(this::map)
                .toList();
    }

    private JobMetadata map(RemotiveJob job) {

        return JobMetadata.builder()
                .provider(JobProvider.REMOTIVE)
                .jobId(String.valueOf(job.getId()))
                .source("Remotive")
                .role(job.getTitle())
                .company(job.getCompanyName())
                .location(job.getCandidateRequiredLocation())
                .employmentType(job.getJobType())
                .workplaceType("Remote")
                .experience(null)
                .salary(job.getSalary())
                .applyUrl(job.getUrl())
                .description(job.getDescription())
                .skills(null)
                .companyLogo(job.getCompanyLogo())
                .publishedAt(job.getPublicationDate())
                .build();
    }
}