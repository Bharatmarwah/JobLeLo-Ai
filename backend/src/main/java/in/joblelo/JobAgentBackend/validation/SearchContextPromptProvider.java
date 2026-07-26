package in.joblelo.JobAgentBackend.validation;

public final class SearchContextPromptProvider {

    private SearchContextPromptProvider() {
    }

    public static final String PROMPT = """
            Extract job search context fields from the query and memory. The query has already been validated as a job search — only extract fields.

            INPUT:
            Query: {{userQuery}}
            Memory: {{memory}}

            Extract these fields, keeping query and memory values completely separate:

            queryRole — normalized/corrected role extracted from the Query only (or null)
            profileRole — role literally stated in Memory only (or null)
            queryLocation — location from the Query only (or null)
            profileLocation — location literally stated in Memory only (or null)
            queryEmploymentType — employment type from Query: "Remote"/"Hybrid"/"Onsite"/"Full-time"/"Part-time" (or null)
            profileEmploymentType — employment type from Memory only (or null)
            queryExperience — years of experience as integer from Query only, if explicitly stated (or null)
            profileExperience — from Memory only (or null)
            querySkills — list of skills from Query only (empty list if none)
            profileSkills — list of skills from Memory only (empty list if none)

            STRICT RULES:
            - queryX fields come ONLY from the Query input. profileX fields come ONLY from Memory.
            - Never cross-fill. Never let a value from query leak into profile*, or vice versa.
            - Never invent values.
            - Normalize queryRole: fix spelling mistakes (e.g., "excutive" → "executive"), expand abbreviations (e.g., "dev" → "developer"), use standard job title terms.
            - Do not estimate experience from vague terms like "experienced" or "fresher".
            - Null for missing fields. [] for missing skills.
            - Do not merge querySkills and profileSkills.
            - Only include skills explicitly named as skills.

            Return ONLY this JSON:
            {
              "queryRole": null,
              "profileRole": null,
              "queryLocation": null,
              "profileLocation": null,
              "queryEmploymentType": null,
              "profileEmploymentType": null,
              "queryExperience": null,
              "profileExperience": null,
              "querySkills": [],
              "profileSkills": []
            }

            No markdown, no explanations, no extra fields.
            """;
}
