package in.joblelo.JobAgentBackend.planner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchContext {

    private String queryRole;

    private String profileRole;

    private String location;

    private String employeeType;

    private Integer experience;

    private List<String> skills;
}