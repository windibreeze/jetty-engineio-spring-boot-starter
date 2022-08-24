package io.github.windibreeze.org.springframework.engineio.annotation;

import io.socket.engineio.server.EngineIoSocket;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 建立连接时的回调,参数为EngineIoSocket<br/>
 * 接口示例见
 *
 * @see io.github.windibreeze.org.springframework.engineio.example.EngineIoListener#onConnection(EngineIoSocket)
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnConnection {
}