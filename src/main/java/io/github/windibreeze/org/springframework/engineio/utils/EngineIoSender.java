package io.github.windibreeze.org.springframework.engineio.utils;

import io.socket.engineio.server.EngineIoSocket;
import io.socket.engineio.server.parser.Packet;

import java.util.Map;

public class EngineIoSender {
    private final Map<String, EngineIoSocket> socketMap;

    public EngineIoSender(Map<String, EngineIoSocket> socketMap) {
        this.socketMap = socketMap;
    }

    public void send(String id, String message) {
        if (!this.socketMap.containsKey(id)) {
            return;
        }
        this.socketMap.get(id).send(new Packet<String>(Packet.MESSAGE, message));
    }

}
