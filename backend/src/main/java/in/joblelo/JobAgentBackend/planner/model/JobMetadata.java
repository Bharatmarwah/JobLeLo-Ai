package in.joblelo.JobAgentBackend.planner.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobMetadata {

    /* ---------- Source ---------- */

    // Which integration produced this job
    private JobProvider provider;

    // Provider-specific unique id
    private String jobId;

    // Original source (talent.com, remotive, company site, etc.)
    private String source;

    /* ---------- Job ---------- */

    private String role;

    private String company;

    private String location;

    // Full Time, Contract, Internship, Freelance...
    private String employmentType;

    // "Remote", "Hybrid", "On-site"
    private String workplaceType;

    // Human readable salary
    private String salary;

    // Years of experience if available
    private String experience;

    // Extracted or provider supplied skills
    private List<String> skills;

    /* ---------- Content ---------- */

    private String description;

    /* ---------- Links ---------- */

    private String applyUrl;

    private String companyLogo;

    /* ---------- Metadata ---------- */

    // ISO-8601 String or Instant if you prefer
    private String publishedAt;
}