package in.joblelo.JobAgentBackend.controller;

import in.joblelo.JobAgentBackend.service.GoogleMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/gmail")
@RequiredArgsConstructor
public class GoogleMailController {

    private final GoogleMailService googleMailService;

    @GetMapping("/connect")
    public Map<String, String> connectGmail(){
        String authUrl = googleMailService.connectGmail();
        return Map.of("authUrl", authUrl);
    }

    @GetMapping("/callback")
    public Map<String, Object> callbackForGmail(@RequestParam("code") String code){
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Authorization code is required");
        }
        return googleMailService.gmailCallback(code);
    }

    @GetMapping("/status")
    public Map<String, Boolean> gmailStatus() {
        return googleMailService.status();
    }

}
