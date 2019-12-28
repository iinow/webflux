package com.ha.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ha.entity.Memo;
import com.ha.repository.MemoRepository;

import reactor.core.publisher.Mono;

@Service
public class MemoService {

	private MemoRepository repository;

	public MemoService(
			final MemoRepository repository) {
		this.repository = repository;
	}
	
	public List<Memo> findAll(){
		return repository.findAll();
	}

	public Mono<Memo> add(Memo memo){
		return Mono.fromCallable(() -> repository.save(memo));
	}

	public Mono<Memo> getById(Long id){
		return Mono.fromCallable(() -> repository.findById(id).orElseThrow());
	}
}
