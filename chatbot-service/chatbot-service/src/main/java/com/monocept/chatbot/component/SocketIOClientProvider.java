package com.monocept.chatbot.component;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.monocept.chatbot.utils.SocketIoUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class SocketIOClientProvider {

    private final SocketIOServer socketIOServer;
    private final SocketIoUtility socketIOUtil;

    public SocketIOClientProvider(SocketIOServer socketIOServer, SocketIoUtility socketIOUtil) {
        this.socketIOServer = socketIOServer;
        this.socketIOUtil = socketIOUtil;
    }

    public SocketIOClient getClientByUserId(String userId) {
        String sessionId = socketIOUtil.getSessionId(userId);
        if (sessionId == null) return null;

        try {
            UUID uuid = UUID.fromString(sessionId);
            return socketIOServer.getClient(uuid);
        } catch (IllegalArgumentException e) {
            log.error("Failed to parse session ID [{}] for userId [{}]: {}", sessionId, userId, e.getMessage());
            return null;
        }
    }
}
