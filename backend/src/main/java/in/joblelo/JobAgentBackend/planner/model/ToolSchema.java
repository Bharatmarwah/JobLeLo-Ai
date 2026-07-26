package in.joblelo.JobAgentBackend.planner.model;

import lombok.Data;

import java.util.List;

@Data
public class ToolSchema{

    private String role;
    private String location;
    private String experienceType;
    private Integer experience;
    private List<String> skills;

}
