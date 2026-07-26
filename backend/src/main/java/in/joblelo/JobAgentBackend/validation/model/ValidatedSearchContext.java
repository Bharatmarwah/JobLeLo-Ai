package in.joblelo.JobAgentBackend.validation.model;

import lombok.Data;

import java.util.List;

@Data
public class ValidatedSearchContext {

    private String queryRole;//queryRole
    private String profileRole;//profileRole

    private String queryLocation;//queryLocation
    private String profileLocation;//profileLocation

    private String queryEmploymentType;//queryEmploymentType
    private String profileEmploymentType;//profileEmploymentType

    private Integer queryExperience;//queryExperience
    private Integer profileExperience;//profileExperience

    private List<String> querySkills;//querySkills
    private List<String> profileSkills;//profileSkills
}