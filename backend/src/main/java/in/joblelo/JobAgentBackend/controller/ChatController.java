package in.joblelo.JobAgentBackend.controller;

import in.joblelo.JobAgentBackend.Orchestration.ChatOrchestrator;
import in.joblelo.JobAgentBackend.model.ChatRequest;
import in.joblelo.JobAgentBackend.model.ChatResponse;
import in.joblelo.JobAgentBackend.model.SessionResponse;
import in.joblelo.JobAgentBackend.planner.manager.GmailTokenManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
public class ChatController {

    private final ChatOrchestrator chatOrchestrator;
    private final GmailTokenManager gmailTokenManager;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody @Valid
                                 ChatRequest chatRequest) {
        return chatOrchestrator.processChat(chatRequest);
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/create-session")
    public SessionResponse createSession(){
        String sessionId = UUID.randomUUID().toString();
        System.out.println(sessionId);
        return new SessionResponse(sessionId);
    }

    //test: Avoided
    @GetMapping("/token")
    public String getToken(){
      return gmailTokenManager.getAccessToken();
    }

}
