package com.monocept.chatbot.component;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

public class SocketIOClient {

    private final Socket socket;

    public SocketIOClient(String uri, String userId) throws URISyntaxException {
        IO.Options options = new IO.Options();
        options.query = "userId="+userId;

        socket = IO.socket(uri, options);
        setupListeners();
    }

    private void setupListeners() {

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Connected to the server in CLIENT!");
            }
        });

        // Listen for a custom event 'chat_message' that the server sends
        socket.on("chat_message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // Print the data received from the server (e.g., a response message)
                if (args != null && args.length > 0) {
                    System.out.println("IN CLIENT Received message from server: " + args[0].toString());
                }
            }
        });

//         Listen for the 'disconnect' event when the client disconnects
        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Disconnected from the server. IN CLIENT");
            }
        });
    }

    public void connect() {
        socket.connect();
    }

    public void sendMessage(String eventName, String message) {
        socket.emit(eventName, message);
    }

    public void disconnect() {
        socket.disconnect();
    }

}

