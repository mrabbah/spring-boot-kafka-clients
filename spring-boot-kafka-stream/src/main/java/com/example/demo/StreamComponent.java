package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.service.StreamService;

@Component
public class StreamComponent  implements CommandLineRunner {


	@Autowired
	StreamService streamService;
	
	@Override
    public void run(String...args) throws Exception {
		streamService.stream();

    }
}
