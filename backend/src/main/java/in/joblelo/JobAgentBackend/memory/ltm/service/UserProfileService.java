package in.joblelo.JobAgentBackend.memory.ltm.service;

import in.joblelo.JobAgentBackend.conversation.model.JobEntities;
import in.joblelo.JobAgentBackend.conversation.model.ProfileUpdateOperations;
import in.joblelo.JobAgentBackend.memory.ltm.entity.UserProfile;
import in.joblelo.JobAgentBackend.memory.ltm.repository.UserProfileRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserProfileRepo userProfileRepo;

    public void updateProfile(String userId, JobEntities entities, ProfileUpdateOperations operations){
        UserProfile userProfile =
                userProfileRepo.findByUserId(userId).orElseGet(()-> createUserProfile(userId));

        merge(userProfile, entities, operations);

        userProfileRepo.save(userProfile);
    }

    private static UserProfile createUserProfile(String userId){
        log.info("UserProfile is created for userId: {}",userId);
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userId);
        return userProfile;
    }

    private void merge(
            UserProfile profile,
            JobEntities entities,
            ProfileUpdateOperations operations) {

        log.info("Merge is called");

        switch (operations.getRoles()) {
            case REPLACE -> profile.setPreferredRoles(new ArrayList<>(entities.getRoles()));
            case APPEND -> append(profile.getPreferredRoles(), entities.getRoles());
            case REMOVE -> remove(profile.getPreferredRoles(), entities.getRoles());
            case CLEAR -> profile.getPreferredRoles().clear();
            case NONE -> {}
        }

        switch (operations.getLocations()) {
            case REPLACE -> profile.setPreferredLocations(new ArrayList<>(entities.getLocations()));
            case APPEND -> append(profile.getPreferredLocations(), entities.getLocations());
            case REMOVE -> remove(profile.getPreferredLocations(), entities.getLocations());
            case CLEAR -> profile.getPreferredLocations().clear();
            case NONE -> {}
        }

        switch (operations.getSkills()) {
            case REPLACE -> profile.setSkills(new ArrayList<>(entities.getSkills()));
            case APPEND -> append(profile.getSkills(), entities.getSkills());
            case REMOVE -> remove(profile.getSkills(), entities.getSkills());
            case CLEAR -> profile.getSkills().clear();
            case NONE -> {}
        }

        switch (operations.getExperience()) {
            case REPLACE -> profile.setExperience(entities.getExperience());
            case CLEAR -> profile.setExperience(null);
            case NONE -> {}
        }

        switch (operations.getSalaryExpectation()) {
            case REPLACE -> profile.setSalaryExpectation(entities.getSalaryExpectation());
            case CLEAR -> profile.setSalaryExpectation(null);
            case NONE -> {}
        }

        switch (operations.getEmploymentType()) {
            case REPLACE -> profile.setEmploymentType(entities.getEmploymentType());
            case CLEAR -> profile.setEmploymentType(null);
            case NONE -> {}
        }

        switch (operations.getNoticePeriod()) {
            case REPLACE -> profile.setNoticePeriod(entities.getNoticePeriod());
            case CLEAR -> profile.setNoticePeriod(null);
            case NONE -> {}
        }
    }

    private void append(List<String> current, List<String> incoming) {
        if (incoming == null || incoming.isEmpty()) {
            return;
        }

        if (current == null) {
            current = new ArrayList<>();
        }

        for (String value : incoming) {
            if (!current.contains(value)) {
                current.add(value);
            }
        }
    }

    private void remove(List<String> current, List<String> incoming) {
        if (current == null || incoming == null) {
            return;
        }
        current.removeAll(incoming);
    }
    
    public UserProfile getUserProfileDetails(String currentUserId){
        Optional<UserProfile> userProfile = userProfileRepo.findByUserId(currentUserId);
        if (userProfile.isEmpty()){
            log.info("UserProfile is not found for userId: {}",currentUserId);
            return null;
        }
        return userProfile.get();
    }
}
