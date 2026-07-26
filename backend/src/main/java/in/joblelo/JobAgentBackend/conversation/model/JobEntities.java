package in.joblelo.JobAgentBackend.conversation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobEntities {

    private List<String> roles = new ArrayList<>();

    private String experience = "";

    private List<String> locations = new ArrayList<>();

    private String salaryExpectation = "";

    private String employmentType = "";

    private String noticePeriod = "";

    private List<String> skills = new ArrayList<>();
}
