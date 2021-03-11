package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProduserServiceTests {

	@Autowired
	ProducerService producer;
	
	@Test
	void rafalMessages() {
		try {
			producer.rafalMessages(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
