package com.ha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.ha.config.handler.MemoHandler;

//@Configuration
//implements WebFluxConfigurer
public class WebConfig {

//	@Bean
//	public RouterFunction<ServerResponse> routes(MemoHandler handler){
//		return route(path("/memo2"), 
//				req -> ok().bodyValue("HELEHELE"));
//		return RouterFunctions.route()
//			.GET("/memo", handler::memo)
//			.build();
//	}
}
