package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConsumerServiceTests {

	@Autowired
	ConsumerService consumer;
	
	@Test
	void consume() {
		try {
			consumer.consume();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
