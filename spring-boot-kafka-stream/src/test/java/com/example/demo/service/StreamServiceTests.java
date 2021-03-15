package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StreamServiceTests {

	@Autowired
	StreamService streamService;
	
	@Test
	void stream() {
		try {
			streamService.stream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
