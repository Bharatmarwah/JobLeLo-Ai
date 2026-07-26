package in.joblelo.JobAgentBackend.planner.builder;

import in.joblelo.JobAgentBackend.planner.model.ToolSchema;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GmailQueryBuilder {



    public String build(ToolSchema schema) {

        List<String> query = new ArrayList<>();

        query.add("in:inbox");
        query.add("newer_than:90d");

        // Role — AND'd. A job alert that doesn't mention the role isn't a match.
        if (schema.getRole() != null && !schema.getRole().isBlank()) {
            query.add("\"" + schema.getRole() + "\"");
        }

        // Location — AND'd, same reasoning as role.
        if (schema.getLocation() != null && !schema.getLocation().isBlank()) {
            query.add("\"" + schema.getLocation() + "\"");
        }

        // Skills — OR'd inside one group. Requiring all skills in a single
        // email was silently zeroing out results before.
        if (schema.getSkills() != null && !schema.getSkills().isEmpty()) {
            String skillsGroup = schema.getSkills().stream()
                    .map(skill -> "\"" + skill + "\"")
                    .collect(Collectors.joining(" OR "));
            query.add("(" + skillsGroup + ")");
        }

        return String.join(" ", query);
    }
}