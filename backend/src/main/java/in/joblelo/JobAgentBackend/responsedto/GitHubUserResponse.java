package in.joblelo.JobAgentBackend.responsedto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubUserResponse {

    private Long id;
    private String login;
    private String name;
    private String email;
    private String avatar_url;

}