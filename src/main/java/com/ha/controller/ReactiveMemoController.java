package com.ha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ha.entity.Memo;
import com.ha.service.MemoService;

import reactor.core.publisher.Mono;

@RestController
public class ReactiveMemoController {
	
	private MemoService memoService;
	
	public ReactiveMemoController(
			final MemoService memoService) {
		this.memoService = memoService;
	}
	
	@GetMapping("/hello2")
	public List<Memo> hello2() {
		return memoService.findAll();
	}
	
	@GetMapping(value = "/v2/memos/{id}")
	public Mono<Memo> getMemo(
			@PathVariable(name = "id", required = true) Long id){
		return memoService.findById(id);
	}
}
