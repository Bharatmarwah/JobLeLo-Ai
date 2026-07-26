package in.joblelo.JobAgentBackend.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotEnvConfig {
    static{
        Dotenv dotenv = Dotenv
                .configure()
                .directory(".")
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> {
            // Only set properties that are not security secrets or already defined by env
            // For secrets, rely on proper Spring property or environment variable injection
            String key = entry.getKey();
            String value = entry.getValue();
            if (System.getProperty(key) == null) {
                System.setProperty(key, value);
            }
        });
    }
}

