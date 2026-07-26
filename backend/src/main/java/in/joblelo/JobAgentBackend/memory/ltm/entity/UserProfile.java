package in.joblelo.JobAgentBackend.memory.ltm.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userProfileId;

    @Column(nullable = false, unique = true)
    private String userId;

    @ElementCollection
    @CollectionTable(
            name = "user_profile_roles",
            joinColumns = @JoinColumn(name = "user_profile_id")
    )
    @Column(name = "role")
    private List<String> preferredRoles = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "user_profile_locations",
            joinColumns = @JoinColumn(name = "user_profile_id")
    )
    @Column(name = "location")
    private List<String> preferredLocations = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "user_profile_skills",
            joinColumns = @JoinColumn(name = "user_profile_id")
    )
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @Column(name = "experience")
    private String experience;

    @Column(name = "salary_expectation")
    private String salaryExpectation;

    @Column(name = "employment_type")
    private String employmentType;

    @Column(name = "notice_period")
    private String noticePeriod;
}