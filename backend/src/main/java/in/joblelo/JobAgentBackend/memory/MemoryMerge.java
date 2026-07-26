package in.joblelo.JobAgentBackend.memory;

import in.joblelo.JobAgentBackend.memory.ltm.entity.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemoryMerge {

    public String mergeMemory(String contextSummary, UserProfile userProfile) {
        return build(contextSummary, userProfile);
    }

    private static String build(String contextSummary, UserProfile userProfile) {

        StringBuilder sb = new StringBuilder();

        // ---------------- Conversation ----------------
        if (contextSummary != null && !contextSummary.isBlank()) {
            sb.append("Current Conversation\n");
            sb.append("--------------------\n");
            sb.append(contextSummary.trim()).append("\n\n");
        }

        // ---------------- User Profile ----------------
        if (userProfile != null) {

            sb.append("User Profile\n");
            sb.append("------------\n");

            appendList(
                    sb,
                    "Preferred Roles",
                    userProfile.getPreferredRoles()
            );

            appendList(
                    sb,
                    "Skills",
                    userProfile.getSkills()
            );

            appendList(
                    sb,
                    "Preferred Locations",
                    userProfile.getPreferredLocations()
            );

            appendField(
                    sb,
                    "Experience",
                    userProfile.getExperience()
            );

            appendField(
                    sb,
                    "Salary Expectation",
                    userProfile.getSalaryExpectation()
            );

            appendField(
                    sb,
                    "Employment Type",
                    userProfile.getEmploymentType()
            );

            appendField(
                    sb,
                    "Notice Period",
                    userProfile.getNoticePeriod()
            );
        }

        return sb.toString().trim();
    }

    private static void appendField(
            StringBuilder sb,
            String title,
            String value
    ) {

        if (value == null || value.isBlank()) {
            return;
        }

        sb.append(title)
                .append(": ")
                .append(value.trim())
                .append("\n\n");
    }

    private static void appendList(
            StringBuilder sb,
            String title,
            Iterable<String> values
    ) {

        if (values == null) {
            return;
        }

        boolean hasValue = false;

        for (String value : values) {

            if (!hasValue) {
                sb.append(title).append(":\n");
                hasValue = true;
            }

            if (value != null && !value.isBlank()) {
                sb.append("- ")
                        .append(value.trim())
                        .append("\n");
            }
        }

        if (hasValue) {
            sb.append("\n");
        }
    }
}