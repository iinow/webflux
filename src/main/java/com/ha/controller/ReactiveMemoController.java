package com.ha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.web.bind.annotation.*;

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

	@PostMapping(value = "")
	public Mono<Memo> post(
			@RequestBody Memo memo){
		return Mono.just(memo);
	}
}
