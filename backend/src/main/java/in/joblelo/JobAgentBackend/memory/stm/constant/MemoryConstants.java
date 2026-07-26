package in.joblelo.JobAgentBackend.memory.stm.constant;

import java.time.Duration;

public final class MemoryConstants {

    private MemoryConstants() {
    }

    public static final String SESSION_PREFIX = "chat:session:";
    public static final Duration TTL = Duration.ofHours(24);
}