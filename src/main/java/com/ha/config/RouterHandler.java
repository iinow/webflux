package com.ha.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ha.config.handler.MemoHandler;
import com.ha.entity.Memo;

@Component
public class RouterHandler {
	
	@Bean
	public RouterFunction<ServerResponse> memoRouter(@Autowired MemoHandler memoHandler) {
		return RouterFunctions.route().GET("/memos/{id}", memoHandler::memo)
				.build();
	}
}
