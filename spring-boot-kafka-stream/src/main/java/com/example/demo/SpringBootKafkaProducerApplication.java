package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.service.StreamService;

@SpringBootApplication
public class SpringBootKafkaProducerApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootKafkaProducerApplication.class, args);
		
	}

}
