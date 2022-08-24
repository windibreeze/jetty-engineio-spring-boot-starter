package io.github.windibreeze.org.springframework.engineio.config;

import io.github.windibreeze.org.springframework.engineio.annotation.OnClose;
import io.github.windibreeze.org.springframework.engineio.annotation.OnConnection;
import io.github.windibreeze.org.springframework.engineio.annotation.OnMessage;
import io.github.windibreeze.org.springframework.engineio.annotation.OnOpen;
import io.github.windibreeze.org.springframework.engineio.processor.EndpointProcessor;
import io.github.windibreeze.org.springframework.engineio.processor.EngineIoAnnotationProcessor;
import io.github.windibreeze.org.springframework.engineio.utils.EngineIoSender;
import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.EngineIoSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Import({EndpointProcessor.class, EngineIoAnnotationProcessor.class})
public class EngineIoConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(EngineIoConfig.class);

    private final Map<String, EngineIoSocket> socketMap = new HashMap<>();

    @Bean
    public EngineIoServer engineIoServer(EndpointProcessor endpointProcessor) {
        EngineIoServer engineIoServer = new EngineIoServer();
        this.initEvent(engineIoServer, endpointProcessor);
        return engineIoServer;
    }

    public void initEvent(EngineIoServer engineIoServer, EndpointProcessor endpointProcessor) {
        engineIoServer.on("connection", args -> {
            LOGGER.trace("on connection {}", args);
            endpointProcessor.handleEndpoint(OnConnection.class, args);

            EngineIoSocket socket = (EngineIoSocket) args[0];
            String id = socket.getId();
            socketMap.put(id, socket);

            socket.on("open", args1 -> {
                LOGGER.trace("on open {}", args1);
                endpointProcessor.handleEndpoint(OnOpen.class, socket);
            });

            socket.on("message", args1 -> {
                LOGGER.trace("on message {}", args1);
                endpointProcessor.handleEndpoint(OnMessage.class, socket, args1[0]);
            });

            socket.on("close", args1 -> {
                LOGGER.trace("on close {}", args1);
                endpointProcessor.handleEndpoint(OnClose.class, socket, args1[0], args1[1]);
                socketMap.remove(id);
            });
        });
    }

    @Bean
    public EngineIoSender engineIoSender() {
        return new EngineIoSender(socketMap);
    }

}
