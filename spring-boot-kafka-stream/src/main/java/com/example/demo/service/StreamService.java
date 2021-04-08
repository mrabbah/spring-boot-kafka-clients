package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import com.example.demo.config.KafkaConfig;
import com.example.demo.config.KafkaProperties;
import com.example.demo.dto.User;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;


import lombok.extern.java.Log;

@Service
@Log
public class StreamService {

	@Autowired
	private KafkaProperties config;
	@Autowired
	private KafkaConfig kafkaStreamsConfig;
	
	public void stream() {
		Properties props = kafkaStreamsConfig.getProperties();
		log.info("Creating Kafka stream withe following configuration -> " + props.toString() );
		StreamsBuilder builder = new StreamsBuilder();
		final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
                config.getSchemaRegistryEndPoint());
		final SpecificAvroSerde<User> userSerd = new SpecificAvroSerde<User>();
		userSerd.configure(serdeConfig, false);
		builder.stream(config.getSourceTopic(), Consumed.with(Serdes.String(), userSerd))
		.filter((key, user) -> user.getAge() < 40)
        .mapValues(user -> new User(user.getName(), user.getAge()))
        .to(config.getTargetTopic(), Produced.with(Serdes.String(), userSerd));
		
		KafkaStreams streams;
		
		if(kafkaStreamsConfig.getSupplier() != null) {
			streams = new KafkaStreams(builder.build(), props, kafkaStreamsConfig.getSupplier());
		} else {
			streams = new KafkaStreams(builder.build(), props);
		}
		streams.start();
	}
	
}
