package com.ha;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ha.entity.Memo;
import com.ha.repository.MemoRepository;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
public class ReactiveApplication {
	
	@Autowired
	private MemoRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(ReactiveApplication.class, args);
	}

	@PostConstruct
	public void init() {
		Memo m = new Memo();
		m.setName("Helelele");
		repository.save(m);
	}
}
