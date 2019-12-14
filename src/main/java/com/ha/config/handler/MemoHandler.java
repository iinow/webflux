package com.ha.config.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.ha.entity.Memo;
import com.ha.repository.MemoRepository;
import com.ha.repository.ReactiveMemoRepository;

import reactor.core.publisher.Mono;

@Component
public class MemoHandler {
	
	private MemoRepository repository;
	private ReactiveMemoRepository reactiveRepository;
	
	public MemoHandler(
			final MemoRepository repository, 
			final ReactiveMemoRepository reactiveRepository) {
		this.reactiveRepository = reactiveRepository;
		this.repository = repository;
	}
	
	private static final ServerResponse.BodyBuilder jsonBuilder = ServerResponse.ok().contentType(MediaType.APPLICATION_JSON);

	public Mono<ServerResponse> memo(ServerRequest request){
		Long id = Long.parseLong(request.pathVariable("id"));
		Memo m = repository.findById(id).get();
		return jsonBuilder.body(Mono.just(m), Memo.class);
	}
	
	public Mono<Memo> findById(Long id){
		return reactiveRepository.findById(id);
	}
}
