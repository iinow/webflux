package com.ha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ha.entity.Memo;
import com.ha.service.MemoService;

@RestController
public class ReactiveMemoController {
	
	@Autowired
	private MemoService memoService;
	
//	@GetMapping("/hello")
//    public Mono<Memo> hello() {
//        return Mono.just(memoService.findAll().get(0));
//    }
	
	@GetMapping("/hello2")
	public List<Memo> hello2() {
		return memoService.findAll();
	}
}
