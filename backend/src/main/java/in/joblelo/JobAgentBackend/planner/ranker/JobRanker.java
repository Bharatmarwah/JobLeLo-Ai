package in.joblelo.JobAgentBackend.planner.ranker;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import in.joblelo.JobAgentBackend.planner.model.JobMetadata;
import in.joblelo.JobAgentBackend.planner.model.JobProvider;
import in.joblelo.JobAgentBackend.planner.model.RankedJob;
import in.joblelo.JobAgentBackend.planner.model.SearchContext;
import in.joblelo.JobAgentBackend.service.AnalyserGenerationService;
import in.joblelo.JobAgentBackend.utils.ResponseCleanerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobRanker {

    private final AnalyserGenerationService analyserGenerationService;
    private final ObjectMapper objectMapper;
    private final ResponseCleanerUtil responseCleanerUtil;

    private final ObjectMapper lenientMapper = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
            .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .build();

    public List<RankedJob> rank(
            SearchContext searchContext,
            List<JobMetadata> jobs
    ) {

        if (jobs == null || jobs.isEmpty()) {
            return Collections.emptyList();
        }

        int MAX_JOBS = 15;
        if (jobs.size() > MAX_JOBS) {
            Map<JobProvider, Queue<JobMetadata>> byProvider = new LinkedHashMap<>();
            for (JobMetadata job : jobs) {
                byProvider.computeIfAbsent(job.getProvider(), k -> new LinkedList<>()).add(job);
            }
            List<JobMetadata> capped = new ArrayList<>(MAX_JOBS);
            List<JobProvider> providers = new ArrayList<>(byProvider.keySet());
            while (capped.size() < MAX_JOBS) {
                boolean added = false;
                for (JobProvider p : providers) {
                    Queue<JobMetadata> q = byProvider.get(p);
                    if (q != null && !q.isEmpty()) {
                        capped.add(q.poll());
                        added = true;
                        if (capped.size() >= MAX_JOBS) break;
                    }
                }
                if (!added) break;
            }
            log.info("[JobRanker] Capped {} jobs to {} (round-robin across providers)", jobs.size(), capped.size());
            jobs = capped;
        }

        try {

            String prompt = buildPrompt(searchContext, jobs);

            String rawResponse =
                    analyserGenerationService.generate(prompt);

            String cleanedResponse =
                    responseCleanerUtil.extractJson(rawResponse);

            if (log.isDebugEnabled()) {
                log.debug("LLM response (truncated): {}", rawResponse.substring(0, Math.min(rawResponse.length(), 500)));
            }

            try {
                return objectMapper.readValue(
                        cleanedResponse,
                        new TypeReference<List<RankedJob>>() {
                        }
                );
            } catch (Exception e) {
                log.warn("Standard JSON parsing failed, trying lenient parser. Response: {}", 
                    cleanedResponse.substring(0, Math.min(cleanedResponse.length(), 300)));
                return parseLenient(cleanedResponse);
            }

        } catch (Exception e) {

            log.error(
                    "Failed to rank {} jobs.",
                    jobs.size(),
                    e
            );

            return Collections.emptyList();
        }
    }

    private List<RankedJob> parseLenient(String json) {
        try {
            return lenientMapper.readValue(
                    json,
                    new TypeReference<List<RankedJob>>() {
                    }
            );
        } catch (Exception e) {
            log.warn("Lenient parse also failed, trying repaired JSON");
        }
        try {
            String repaired = repairTruncatedJson(json);
            return objectMapper.readValue(
                    repaired,
                    new TypeReference<List<RankedJob>>() {
                    }
            );
        } catch (Exception e2) {
            log.warn("Repair failed, trying to extract array from object wrapper", e2);
        }
        List<RankedJob> extracted = extractArrayFromObject(json);
        if (!extracted.isEmpty()) {
            return extracted;
        }
        log.error("All JSON parsing attempts failed for ranking");
        return Collections.emptyList();
    }

    private List<RankedJob> extractArrayFromObject(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root instanceof ObjectNode obj) {
                java.util.Iterator<String> fieldNames = obj.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    JsonNode element = obj.get(fieldName);
                    if (element instanceof ArrayNode array) {
                        List<RankedJob> jobs = new ArrayList<>();
                        for (JsonNode item : array) {
                            try {
                                RankedJob job = objectMapper.treeToValue(item, RankedJob.class);
                                jobs.add(job);
                            } catch (Exception ignored) {
                            }
                        }
                        if (!jobs.isEmpty()) {
                            return jobs;
                        }
                    }
                }
                log.warn("No array field found in object wrapper. Fields: {}", obj.fieldNames());
            }
            if (root instanceof ObjectNode) {
                try {
                    RankedJob single = objectMapper.treeToValue(root, RankedJob.class);
                    if (single != null && single.getJob() != null) {
                        return Collections.singletonList(single);
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract array from object wrapper", e);
        }
        return Collections.emptyList();
    }

    private String repairTruncatedJson(String json) {
        String trimmed = json.trim();
        int openArrays = 0;
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
                    case '[' -> openArrays++;
                    case ']' -> openArrays--;
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
        while (openArrays > 0) {
            sb.append(']');
            openArrays--;
        }
        return sb.toString();
    }

    private String buildPrompt(
            SearchContext searchContext,
            List<JobMetadata> jobs
    ) {

        try {

            String contextJson =
                    objectMapper.writeValueAsString(searchContext);

            List<JobMetadata> trimmed = new ArrayList<>(jobs.size());
            for (JobMetadata job : jobs) {
                JobMetadata copy = new JobMetadata();
                copy.setProvider(job.getProvider());
                copy.setJobId(job.getJobId());
                copy.setSource(job.getSource());
                copy.setRole(job.getRole());
                copy.setCompany(job.getCompany());
                copy.setLocation(job.getLocation());
                copy.setEmploymentType(job.getEmploymentType());
                copy.setWorkplaceType(job.getWorkplaceType());
                copy.setSalary(job.getSalary());
                copy.setExperience(job.getExperience());
                copy.setSkills(job.getSkills());
                String desc = job.getDescription();
                if (desc != null && desc.length() > 200) {
                    desc = desc.substring(0, 197) + "...";
                }
                copy.setDescription(desc);
                copy.setApplyUrl(job.getApplyUrl());
                copy.setCompanyLogo(job.getCompanyLogo());
                copy.setPublishedAt(job.getPublishedAt());
                trimmed.add(copy);
            }

            String jobsJson =
                    objectMapper.writeValueAsString(trimmed);

            return JobRankerPrompt.JOB_RANKER_PROMPT + """
                    
                    =========================================================
                    USER SEARCH CONTEXT
                    =========================================================
                    
                    %s
                    
                    =========================================================
                    JOBS TO RANK
                    =========================================================
                    
                    %s
                    """
                    .formatted(
                            contextJson,
                            jobsJson
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to build ranking prompt.",
                    e
            );
        }
    }

}