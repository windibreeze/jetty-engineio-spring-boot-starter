package io.github.windibreeze.org.springframework.engineio.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = EngineIoProperty.PREFIX)
public class EngineIoProperty {
    public static final String PREFIX = "spring.engine-io";
    public static final String SERVER_PORT = "server-port";
    private Integer serverPort;

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }
}
