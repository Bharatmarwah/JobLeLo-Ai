package in.joblelo.JobAgentBackend.planner.mapper;

import in.joblelo.JobAgentBackend.planner.model.*;
import in.joblelo.JobAgentBackend.planner.model.jooble.JoobleJob;
import in.joblelo.JobAgentBackend.planner.model.jooble.JoobleResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class JoobleMapper implements JobMetadataMapper<JoobleResponse> {

    @Override
    public List<JobMetadata> toJobMetadata(JoobleResponse response) {

        if (response == null || response.getJobs() == null) {
            return Collections.emptyList();
        }

        return response.getJobs()
                .stream()
                .map(this::map)
                .toList();
    }

    private JobMetadata map(JoobleJob job) {

        return JobMetadata.builder()
                .provider(JobProvider.JOOBLE)
                .jobId(String.valueOf(job.getId()))
                .source(job.getSource())
                .role(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .employmentType(job.getType())
                .workplaceType(null)
                .experience(null)
                .salary(job.getSalary())
                .applyUrl(job.getLink())
                .description(job.getSnippet())
                .skills(null)
                .companyLogo(null)
                .publishedAt(job.getUpdated())
                .build();
    }
}