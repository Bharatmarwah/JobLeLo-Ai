package in.joblelo.JobAgentBackend.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public String getCurrentUserId() {
       return
               (String)
                       SecurityContextHolder.
                               getContext().
                               getAuthentication().
                               getPrincipal();

    }
}