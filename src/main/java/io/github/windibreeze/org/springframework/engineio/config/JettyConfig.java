package io.github.windibreeze.org.springframework.engineio.config;

import io.github.windibreeze.org.springframework.engineio.property.EngineIoProperty;
import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.JettyWebSocketHandler;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class JettyConfig {

    @Autowired
    private EngineIoProperty engineIoProperty;

    @Bean(name = "engineIoJettyServer")
    public Server server(EngineIoServer engineIoServer) throws Exception {
        Server server = new Server(engineIoProperty.getServerPort());

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath("/");

        final ServletHolder serverHolder = new ServletHolder(new HttpServlet() {
            private static final long serialVersionUID = -2117128003866849710L;

            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                engineIoServer.handleRequest(request, response);
            }
        });
        serverHolder.setAsyncSupported(true);
        servletContextHandler.addServlet(serverHolder, "/engine.io/*");

        try {
            WebSocketUpgradeFilter webSocketUpgradeFilter = WebSocketUpgradeFilter.configure(servletContextHandler);
            webSocketUpgradeFilter.addMapping(
                    new ServletPathSpec("/engine.io/*"),
                    (servletUpgradeRequest, servletUpgradeResponse) -> new JettyWebSocketHandler(engineIoServer));
        } catch (ServletException ex) {
            ex.printStackTrace();
        }

        server.setHandler(servletContextHandler);
        return server;
    }

}
