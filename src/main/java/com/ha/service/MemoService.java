package com.ha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ha.entity.Memo;
import com.ha.repository.MemoRepository;

import lombok.RequiredArgsConstructor;

@Service
public class MemoService {

	@Autowired
	private MemoRepository repository;
	
//	public Flux<Memo> findAll(){
//		return Flux.
//	}
	
	public List<Memo> findAll(){
		return repository.findAll();
	}
}
