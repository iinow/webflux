package com.ha.flux.test;

import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class TestTests {

    @Test
    public void measureTime() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .name("nomal number")
                .metrics()
                .delayElements(Duration.ofSeconds(2))
                .log()
                .subscribe();

        Thread.sleep(10000);
    }

    @Test
    public void route(){
//        nest(path("/orders"));
    }
}
