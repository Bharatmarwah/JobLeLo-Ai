package in.joblelo.JobAgentBackend.memory.stm;

import in.joblelo.JobAgentBackend.memory.stm.constant.MemoryConstants;
import in.joblelo.JobAgentBackend.memory.stm.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StmService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void addMessage(String sessionId, ChatMessage message) {

        String key = MemoryConstants.SESSION_PREFIX + sessionId;

        Long size = redisTemplate.opsForList()
                .rightPush(key, message);

        redisTemplate.expire(key, MemoryConstants.TTL);
    }

    @SuppressWarnings("unchecked")
    public List<ChatMessage> getMessages(String sessionId) {

        String key = MemoryConstants.SESSION_PREFIX + sessionId;

        return
                (List<ChatMessage>) (List<?>)
                        redisTemplate.opsForList()
                                .range(key, -10, -1);
    }

    public void clearSession(String sessionId) {

        String key = MemoryConstants.SESSION_PREFIX + sessionId;

        redisTemplate.delete(key);
    }
}