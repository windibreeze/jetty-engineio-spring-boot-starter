# jetty-engineio-spring-boot-starter

> 基于**jetty-engine.io**制作的快速接入依赖

# 使用方法

### maven依赖

```xml

<dependencies>
    <dependency>
        <artifactId>jetty-engineio-spring-boot-starter</artifactId>
        <groupId>io.github.windibreeze</groupId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

### 服务配置项

```properties
# 开启服务监听的端口，不填则服务默认关闭
spring.engine-io.server-port=9090
```

### 业务处理

> 使用注解在自己的业务方法上标记，即可与客户端实现engine.io的通信

```java
import io.github.windibreeze.org.springframework.engineio.annotation.OnClose;
import io.github.windibreeze.org.springframework.engineio.annotation.OnConnection;
import io.github.windibreeze.org.springframework.engineio.annotation.OnMessage;
import io.github.windibreeze.org.springframework.engineio.annotation.OnOpen;
import io.github.windibreeze.org.springframework.engineio.service.EngineIoService;
import io.socket.engineio.server.EngineIoSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SocketHandle {

    // 注入消息发送者，使用EngineIoSocket的id即可向对应的通道发送消息
    @Autowired
    private EngineIoService engineIoService;

    // 自己业务缓存的socket列表
    private List<String> ids = new ArrayList<>();

    // 客户端创建连接
    @OnConnection
    public void OnConnection(EngineIoSocket socket) {
        log.info("connect {}", socket.getId());
        ids.add(socket.getId());
    }

    // 客户端打开连接
    @OnOpen
    public void OnOpen(EngineIoSocket socket) {
        log.info("open {}", socket.getId());
    }

    // 客户端收到消息
    @OnMessage
    public void OnMessage(EngineIoSocket socket, String message) {
        log.info("message {}:{}", socket.getId(), message);
        // 示例业务，收到send则向所有客户端发送一条消息
        if ("send".equals(message)) {
            ids.forEach(id -> engineIoService.send(id, "hello world"));
        }
    }

    // 客户端关闭
    @OnClose
    public void OnClose(EngineIoSocket socket, String reason, String description) {
        log.info("close {}, {}", reason, description);
        ids.remove(socket.getId());
    }
}
```

### 客户端示例代码
见 [src/test/resources/static/index.html](src/test/resources/static/index.html)


### 参考资料
[Engine.io Java文档](https://socketio.github.io/engine.io-server-java/javadocs/index.html)
