package com.ha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ha.entity.Memo;
import com.ha.repository.MemoRepository;
import com.ha.repository.ReactiveMemoRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
public class MemoService {

	private MemoRepository repository;
	private ReactiveMemoRepository reactiveRepository;
	
	public MemoService(
			final MemoRepository repository, 
			final ReactiveMemoRepository reactiveRepository) {
		this.reactiveRepository = reactiveRepository;
		this.repository = repository;
	}
	
	public List<Memo> findAll(){
		return repository.findAll();
	}
	
	public Mono<Memo> findById(Long id){
		return reactiveRepository.findById(id);
	}
}
