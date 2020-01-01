package com.ha.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;

@SpringBootTest
public class WebSocketClientTests {

    private WebSocketClient socketClient;

    @BeforeEach
    public void init(){
        this.socketClient = new ReactorNettyWebSocketClient();
    }

    @Test
    public void client() throws InterruptedException {
        this.socketClient.execute(
                URI.create("http://localhost:8080/ws/echo"),
                session -> Flux.interval(Duration.ofMillis(100))
                .map(String::valueOf)
                .map(session::textMessage)
                .as(session::send)
        );

        Thread.sleep(10000);
    }
}
