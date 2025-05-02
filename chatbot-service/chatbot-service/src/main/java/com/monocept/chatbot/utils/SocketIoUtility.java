package com.monocept.chatbot.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SocketIoUtility {

    private static final String REDIS_SOCKET_MAP = "socket:user";
    private final RedisTemplate<String, String> redisTemplate;

    public SocketIoUtility(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void registerClient(String userId, String sessionId) {
        redisTemplate.opsForHash().put(REDIS_SOCKET_MAP, userId, sessionId);
    }

    public void removeClient(String userId) {
        redisTemplate.opsForHash().delete(REDIS_SOCKET_MAP, userId);
    }

    public String getSessionId(String userId) {
        Object sessionId = redisTemplate.opsForHash().get(REDIS_SOCKET_MAP, userId);
        return sessionId != null ? sessionId.toString() : null;
    }

    public boolean isConnected(String userId) {
        return redisTemplate.opsForHash().hasKey(REDIS_SOCKET_MAP, userId);
    }

    public void clearAllMappings() {
        redisTemplate.delete(REDIS_SOCKET_MAP);
    }
}

