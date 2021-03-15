package com.example.demo.service;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.kafka.common.header.Header;
import com.example.demo.config.KafkaConfig;
import com.example.demo.config.KafkaProperties;
import com.example.demo.dto.User;

import java.time.Duration;

import lombok.extern.java.Log;

@Service
@Log
public class ConsumerService {

	@Autowired
	private KafkaProperties config;
	@Autowired
	private KafkaConfig kafkaConsumerConfig;
	
	public void consume() throws Exception {
		Properties props = kafkaConsumerConfig.getProperties();
		log.info("----- Creating consumer with following properties: -----");
		log.config(props.toString());
		boolean commit = !Boolean.parseBoolean(config.getEnableAutoCommit());
		KafkaConsumer<String, User> consumer = new KafkaConsumer<String, User>(props);
		consumer.subscribe(Collections.singletonList(config.getTopic()));
		
		while(true) {
			ConsumerRecords<String, User> records = 
					consumer.poll(Duration.ofMillis(Long.MAX_VALUE));
			for (ConsumerRecord<String, User> record : records) {
                log.info("Received user:");
                log.info("\\tpartition: "+ record.partition());
                log.info("\toffset: " + record.offset());
                User person = record.value();
                log.info("\t: " + person.getName() + ", age: " + person.getAge());
                if (record.headers() != null) {
                    log.info("\theaders: ");
                    for (Header header : record.headers()) {
                        log.info("\t\tkey: " + header.key() + ", value: " +  new String(header.value()));
                    }
                }
            }
            if (commit) {
                consumer.commitSync();
            }
            Thread.sleep(2000);
		}
	}
	
	
}
