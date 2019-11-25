package com.ha.config.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ha.entity.Memo;
import com.ha.repository.MemoRepository;

import reactor.core.publisher.Mono;

@Component
public class MemoHandler {
	
	@Autowired
	private MemoRepository repository;
	
	private static final ServerResponse.BodyBuilder jsonBuilder = ServerResponse.ok().contentType(MediaType.APPLICATION_JSON);

	public Mono<ServerResponse> memo(ServerRequest request){
		Long id = Long.parseLong(request.pathVariable("id"));
		Memo m = repository.findById(id).get();
		return jsonBuilder.body(Mono.just(m), Memo.class);
	}
}
