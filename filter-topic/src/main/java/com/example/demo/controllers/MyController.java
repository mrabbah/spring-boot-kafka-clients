package com.example.demo.controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.TransactionDemo;
import com.example.demo.model.TransactionJsonMapper;
import com.example.demo.service.ProducerService;
import com.example.demo.service.StreamService;
import static com.vladkrava.converter.http.AbstractAvroHttpMessageConverter.AVRO_JSON;

@RestController
public class MyController {

	@Autowired
	ProducerService producerService;
	
	@Autowired
	StreamService streamService;
	
	@GetMapping("/api/transaction/publish")
	@ResponseBody String publishTransaction(@RequestParam("txid") String transactionRef, @RequestParam("montant") float montant ) {
		try {
			return producerService.sendMessage(transactionRef, montant);
		} catch (InterruptedException e) {
			return e.getMessage();
		}
		
	}
	
	// @PutMapping(path = "/api/transaction/all", produces = AVRO_JSON)
	@GetMapping("/api/transaction/all")
	@ResponseBody ResponseEntity<List<TransactionJsonMapper>> allTransactions() {
		try {
			return ResponseEntity.ok(streamService.getTransactionsRef());
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			
			e.printStackTrace();
			return ResponseEntity.ok(new ArrayList());
		}
	}
	
	@GetMapping("/api/transaction/exist")
	@ResponseBody ResponseEntity<Boolean> transactionExist(@RequestParam("txid") String transactionRef) {
		return ResponseEntity.ok(streamService.transactionExist(transactionRef));
	}
}
