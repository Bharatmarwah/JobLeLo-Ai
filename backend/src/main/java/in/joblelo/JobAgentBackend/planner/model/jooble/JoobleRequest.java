package in.joblelo.JobAgentBackend.planner.model.jooble;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoobleRequest {

    private String keywords;

    private String location;

    private String radius;

    private boolean companysearch;

    private Integer resultOnPage;

    private Integer searchMode;

    private Integer page;
}