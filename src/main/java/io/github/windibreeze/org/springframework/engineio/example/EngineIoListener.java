package io.github.windibreeze.org.springframework.engineio.example;

import io.socket.engineio.server.EngineIoSocket;

public interface EngineIoListener {

    void onConnection(EngineIoSocket socket);

    void onOpen(EngineIoSocket socket);

    void onMessage(EngineIoSocket socket, String message);

    void onClose(EngineIoSocket socket, String reason, String description);
}
