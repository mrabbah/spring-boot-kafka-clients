package com.example.demo.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import com.example.demo.config.KafkaConfig;
import com.example.demo.config.KafkaProperties;
import com.example.demo.dto.User;

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
	private KafkaConfig config;
	
	private final String MESSAGE = "Hello world kafka";
	private KafkaProducer<String, User> producer;
	private List<Header> headers;
	private boolean blockProducer ;
	private boolean transactionalProducer;
	private int msgPerTx;
	
	public String rafalMessages(int seconds) throws InterruptedException {
		if(producer == null) {
			this.intiProducer();
		}
		if(transactionalProducer) {
            log.info("Using transactional producer. Initializing the transactions ...");
            producer.initTransactions();
        }
		long startingTime = new Date().getTime();
		long i = 0;
		AtomicLong numSent = new AtomicLong(0);
		int min_age = 30;
		int max_age = 60;
		Random rn = new Random();
		do {
			int age = rn.nextInt(max_age - min_age + 1) + min_age;
			User user = new User("RABBAH", age);
			if (transactionalProducer && i % msgPerTx == 0) {
                log.info("Beginning new transaction. Messages sent: " + i);
                producer.beginTransaction();
            }
			log.info("Sending messages number " + i);
			Future<RecordMetadata> recordMetadataFuture = 
					producer.send(new ProducerRecord<String, User>(
							props.getTopic(), 
							null, 
							new Date().getTime(), 
							null, 
							user, 
							headers));
			if(blockProducer) {
                try {
                    recordMetadataFuture.get();
                    // Increment number of sent messages only if ack is received by producer
                    numSent.incrementAndGet();
                } catch (ExecutionException e) {
                    log.warning("Message " + i + " wasn't sent properly! : " + e.getMessage());
                }
            } else {
                // Increment number of sent messages for non blocking producer
                numSent.incrementAndGet();
            }
            if (transactionalProducer && ((i + 1) % msgPerTx == 0)) {
                log.info("Committing the transaction. Messages sent: " + i);
                producer.commitTransaction();
            }
			i++;
			Thread.sleep(200);
		} while((new Date().getTime() - startingTime) < (seconds * 1000));
		log.info(numSent.get() + "  messages sent ... " );
		producer.close();
		return "Result = " + numSent.get() + "  messages sent ... ";
       
	}
	
	private void intiProducer() {		
		
		if (props.getHeaders() != null) {
            headers = new ArrayList<>();

            String[] headersArray = props.getHeaders().split(", [\t\n\r]?");
            for (String header : headersArray) {
                headers.add(new RecordHeader(header.split("=")[0], header.split("=")[1].getBytes()));
            }
        }
		
		producer = new KafkaProducer<String, User>(config.getProperties());
		
        log.info("Sending {} messages ...");

        blockProducer = props.getBlockingProducer() != null;
        
        transactionalProducer = false;
        
        if(props.getAdditionalConfig() != null ) {
        	transactionalProducer = props.getAdditionalConfig().contains("transactional.id");
        }

        msgPerTx = props.getMessagesPerTransaction();
        
        
	}
	
}
