package io.github.windibreeze.org.springframework.engineio;

import io.github.windibreeze.org.springframework.engineio.boot.JettyEngineIoServerAutoBootstrapApplication;
import io.github.windibreeze.org.springframework.engineio.config.EngineIoConfig;
import io.github.windibreeze.org.springframework.engineio.config.JettyConfig;
import io.github.windibreeze.org.springframework.engineio.property.EngineIoProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = EngineIoProperty.PREFIX, value = EngineIoProperty.SERVER_PORT)
@EnableConfigurationProperties({EngineIoProperty.class})
@Import({JettyConfig.class, EngineIoConfig.class, JettyEngineIoServerAutoBootstrapApplication.class})
public class EngineIOServerAutoConfiguration {
}
