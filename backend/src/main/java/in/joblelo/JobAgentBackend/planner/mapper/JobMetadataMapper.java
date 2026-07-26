package in.joblelo.JobAgentBackend.planner.mapper;

import in.joblelo.JobAgentBackend.planner.model.JobMetadata;

import java.util.List;

public interface JobMetadataMapper<T> {

    List<JobMetadata> toJobMetadata(T response);

}