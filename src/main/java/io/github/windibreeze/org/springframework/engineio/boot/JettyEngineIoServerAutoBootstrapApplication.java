package io.github.windibreeze.org.springframework.engineio.boot;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 服务自启
 * 让Spring将其添加为一个可注入的组件，实现CommandLineRunner接口代表在Spring启动后服务就跟着启动。Order则表示注入优先级，数字越小注入的顺序靠前，优先级高
 *
 * @author DeerSunny
 */
@Component
@Order(1)
public class JettyEngineIoServerAutoBootstrapApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(JettyEngineIoServerAutoBootstrapApplication.class);

    private final Server server;

    public JettyEngineIoServerAutoBootstrapApplication(@Qualifier("engineIoJettyServer") Server server) {
        this.server = server;
    }

    @Override
    public void run(String... args) throws Exception {
        if (server != null) {
            LOG.info("|============================================================|");
            LOG.info("| Jetty Engine.io server is starting......                   |");
            server.start();
            LOG.info("| Jetty Engine.io server started successfully.               |");
            LOG.info("|============================================================|");

            // 添加停机钩子，让应用在退出时释放资源
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOG.info("|============================================================|");
                LOG.info("| Jetty Engine.io server is stopping......                   |");
                try {
                    server.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LOG.info("| Jetty Engine.io server stopped successfully.               |");
                LOG.info("|============================================================|");
            }));
        }
    }
}
