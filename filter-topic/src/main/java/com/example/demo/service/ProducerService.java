package com.example.demo.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import com.example.demo.config.ProducerKafkaConfig;
import com.example.demo.config.KafkaProperties;
import com.example.demo.dto.TransactionDemo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.java.Log;

@Service
@Log
public class ProducerService {

	@Autowired
	private KafkaProperties props;
	@Autowired
	private ProducerKafkaConfig config;
	
	private KafkaProducer<String, TransactionDemo> producer;
	private List<Header> headers;
	private boolean blockProducer ;
	private boolean transactionalProducer;
	private int msgPerTx;
	
	public String sendMessage(String transactionRef, float montant) throws InterruptedException {
		if(producer == null) {
			this.intiProducer();
		}
		if(transactionalProducer) {
            log.info("Using transactional producer. Initializing the transactions ...");
            producer.initTransactions();
        }
		TransactionDemo tx = new TransactionDemo(transactionRef, montant);
		Future<RecordMetadata> recordMetadataFuture = 
				producer.send(new ProducerRecord<String, TransactionDemo>(
						props.getTopic(), 
						null, 
						new Date().getTime(), 
						transactionRef, 
						tx, 
						headers));
		if(blockProducer) {
            try {
                recordMetadataFuture.get();
                //TODO
            } catch (ExecutionException e) {
                return e.getMessage();
            }
		}
		// producer.close();
		return "transaction id = " +  transactionRef + " succesfully sent!";
	}
	
	private void intiProducer() {		
		
		if (props.getHeaders() != null) {
            headers = new ArrayList<>();

            String[] headersArray = props.getHeaders().split(", [\t\n\r]?");
            for (String header : headersArray) {
                headers.add(new RecordHeader(header.split("=")[0], header.split("=")[1].getBytes()));
            }
        }
		
		producer = new KafkaProducer<String, TransactionDemo>(config.getProperties());
		
        log.info("Sending {} messages ...");

        blockProducer = props.getBlockingProducer() != null;
        
        transactionalProducer = false;
        
        if(props.getAdditionalConfig() != null ) {
        	transactionalProducer = props.getAdditionalConfig().contains("transactional.id");
        }

        msgPerTx = props.getMessagesPerTransaction();
        
        
	}
	
}
