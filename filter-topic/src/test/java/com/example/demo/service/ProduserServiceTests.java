package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProduserServiceTests {

	@Autowired
	ProducerService producer;
	
	@Test
	void sendMessage() {
		try {
			producer.sendMessage("tx00001", 10f);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
