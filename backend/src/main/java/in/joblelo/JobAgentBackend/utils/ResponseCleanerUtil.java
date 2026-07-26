package in.joblelo.JobAgentBackend.utils;

import in.joblelo.JobAgentBackend.planner.model.JobMetadata;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResponseCleanerUtil {

    public String cleanJson(String response) {
        String cleaned = response
                .replace("```json", "")
                .replace("```", "")
                .trim();

        int startIdx = cleaned.indexOf('{');
        int endIdx = cleaned.lastIndexOf('}');
        if (startIdx != -1 && endIdx > startIdx) {
            cleaned = cleaned.substring(startIdx, endIdx + 1);
        }

        return cleaned;
    }

    /**
     * Extracts the outermost JSON structure (array or object) from a response,
     * using proper bracket-depth matching to find the exact boundaries.
     * Preserves whatever root type the LLM returned.
     */
    public String extractJson(String response) {
        String cleaned = response
                .replace("```json", "")
                .replace("```", "")
                .trim();

        int start = -1;
        char openChar = 0;
        char closeChar = 0;
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (c == '{') {
                start = i;
                openChar = '{';
                closeChar = '}';
                break;
            } else if (c == '[') {
                start = i;
                openChar = '[';
                closeChar = ']';
                break;
            }
        }

        if (start != -1) {
            int depth = 0;
            boolean inString = false;
            for (int i = start; i < cleaned.length(); i++) {
                char c = cleaned.charAt(i);
                if (inString) {
                    if (c == '\\') {
                        i++;
                    } else if (c == '"') {
                        inString = false;
                    }
                } else {
                    if (c == '"') {
                        inString = true;
                    } else if (c == openChar) {
                        depth++;
                    } else if (c == closeChar) {
                        depth--;
                        if (depth == 0) {
                            return cleaned.substring(start, i + 1);
                        }
                    }
                }
            }
        }

        return cleaned;
    }

    public String repairJson(String json) {
        String trimmed = json.trim();
        int openObjects = 0;
        boolean inString = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (inString) {
                if (c == '\\') {
                    i++;
                } else if (c == '"') {
                    inString = false;
                }
            } else {
                switch (c) {
                    case '"' -> inString = true;
                    case '{' -> openObjects++;
                    case '}' -> openObjects--;
                }
            }
        }
        StringBuilder sb = new StringBuilder(trimmed);
        if (inString) {
            sb.append('"');
        }
        while (openObjects > 0) {
            sb.append('}');
            openObjects--;
        }
        return sb.toString();
    }

    public static List<JobMetadata> limitAndTruncate(List<JobMetadata> jobs, int maxJobs, int maxDescLength) {
        return jobs.stream()
                .limit(maxJobs)
                .map(job -> {
                    String desc = job.getDescription();
                    if (desc != null && desc.length() > maxDescLength) {
                        job.setDescription(desc.substring(0, maxDescLength - 3) + "...");
                    }
                    return job;
                })
                .toList();
    }
}
