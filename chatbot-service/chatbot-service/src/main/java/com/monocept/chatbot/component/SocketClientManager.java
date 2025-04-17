package com.monocept.chatbot.component;


import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.corundumstudio.socketio.SocketIOClient;


@Component
public class SocketClientManager {
    private final Map<String, SocketIOClient> userClientMap = new ConcurrentHashMap<>();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void registerClient(String userId, SocketIOClient client) {
        userClientMap.put(userId, client);
    }

    public void removeClient(String userId) {
        userClientMap.remove(userId);
    }

    public SocketIOClient getClient(String userId) {
        return userClientMap.get(userId);
    }

    public boolean isConnected(String userId) {
        return userClientMap.containsKey(userId);
    }

    @PreDestroy
    public void onShutdown() {
        // Cleanup logic for Socket client map
        System.out.println("Shutting down. Cleaning up socket mappings...");
        System.out.println(userClientMap.size());
        // Cleanup Redis mappings: e.g., deleting userId → sessionId from Redis
        for (String userId : userClientMap.keySet()) {
            redisTemplate.opsForHash().delete("session:user", userId);
            System.out.println("Cleared Redis mapping for userId: " + userId);
        }
        userClientMap.clear();

        // Additional cleanup for Redis if needed (e.g., closing connections)
        // If using Jedis or Lettuce, close connections gracefully here.
        // e.g., redisTemplate.getConnectionFactory().destroy();
        System.out.println("Redis cleanup completed.");
    }
}
