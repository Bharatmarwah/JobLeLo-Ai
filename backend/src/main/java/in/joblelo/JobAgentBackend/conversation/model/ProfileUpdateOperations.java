package in.joblelo.JobAgentBackend.conversation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateOperations {

    private UpdateOperation roles = UpdateOperation.NONE;

    private UpdateOperation experience = UpdateOperation.NONE;

    private UpdateOperation locations = UpdateOperation.NONE;

    private UpdateOperation salaryExpectation = UpdateOperation.NONE;

    private UpdateOperation employmentType = UpdateOperation.NONE;

    private UpdateOperation noticePeriod = UpdateOperation.NONE;

    private UpdateOperation skills = UpdateOperation.NONE;

    public boolean hasUpdates() {
        return roles != UpdateOperation.NONE
                || experience != UpdateOperation.NONE
                || locations != UpdateOperation.NONE
                || salaryExpectation != UpdateOperation.NONE
                || employmentType != UpdateOperation.NONE
                || noticePeriod != UpdateOperation.NONE
                || skills != UpdateOperation.NONE;
    }
}