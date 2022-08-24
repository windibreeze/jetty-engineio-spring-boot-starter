package io.github.windibreeze.org.springframework.engineio.service;

import io.socket.engineio.server.EngineIoSocket;
import io.socket.engineio.server.parser.Packet;

import java.util.Map;

public class EngineIoService {
    private final Map<String, EngineIoSocket> socketMap;

    public EngineIoService(Map<String, EngineIoSocket> socketMap) {
        this.socketMap = socketMap;
    }

    public void send(String id, String message) {
        if (!this.socketMap.containsKey(id)) {
            return;
        }
        this.socketMap.get(id).send(new Packet<>(Packet.MESSAGE, message));
    }

    public void close(String id) {
        if (!this.socketMap.containsKey(id)) {
            return;
        }
        this.socketMap.get(id).close();
    }

}
