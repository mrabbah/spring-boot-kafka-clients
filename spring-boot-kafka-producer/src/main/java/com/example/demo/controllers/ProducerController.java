package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ProducerService;

@RestController
public class ProducerController {

	@Autowired
	ProducerService producerService;
	
	@GetMapping("/")
	@ResponseBody String index() {
		try {
			return producerService.rafalMessages(5);
		} catch (InterruptedException e) {
			return e.getMessage();
		}
		
	}
}
