package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import com.example.demo.config.StreamKafkaConfig;
import com.example.demo.config.KafkaProperties;
import com.example.demo.dto.TransactionDemo;
import com.example.demo.model.TransactionJsonMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.extern.java.Log;

@Service
@Log
public class StreamService {

	@Autowired
	private KafkaProperties config;
	@Autowired
	private StreamKafkaConfig kafkaStreamsConfig;
	
	private KafkaStreams streams;
	
	public void stream() {
		Properties props = kafkaStreamsConfig.getProperties();
		log.info("Creating Kafka stream withe following configuration -> " + props.toString() );
		StreamsBuilder builder = new StreamsBuilder();
		final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
				config.getSchemaRegistryEndPoint());
		final SpecificAvroSerde<TransactionDemo> txSerd = new SpecificAvroSerde<TransactionDemo>();
		txSerd.configure(serdeConfig, false);
	
		// final KStream<String, TransactionDemo> stream = builder.stream(config.getSourceTopic(), Consumed.with(Serdes.String(), txSerd));
		
		final GlobalKTable<String, TransactionDemo> transactionsTable = 
				builder.globalTable(config.getSourceTopic(), Consumed.with(Serdes.String(), txSerd),
						Materialized.as(config.getTransactionStore()));
		
		if(kafkaStreamsConfig.getSupplier() != null) {
			streams = new KafkaStreams(builder.build(), props, kafkaStreamsConfig.getSupplier());
		} else {
			streams = new KafkaStreams(builder.build(), props);
		}
		
		streams.start();
	}
	
	public List<TransactionJsonMapper> getTransactionsRef() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<TransactionJsonMapper> result = new ArrayList<TransactionJsonMapper>();
		if(streams != null) {
			ReadOnlyKeyValueStore<String, Object> keyValueStore =
					streams.store(StoreQueryParameters.fromNameAndType(config.getTransactionStore(), 
							QueryableStoreTypes.<String, Object>keyValueStore()));
			
			
			KeyValueIterator<String, Object> it = keyValueStore.all();
			
			while(it.hasNext()) {
				KeyValue<String, Object> next = it.next();
				if (next.value == null) {
                    continue;
                }
				
				//com.example.demo.dto.TransactionDemo tx = (com.example.demo.dto.TransactionDemo) next.value;
				String ref = (String) next.value.getClass().getDeclaredMethod("getRef").invoke(next.value);
				Float montant = (Float) next.value.getClass().getDeclaredMethod("getMontant").invoke(next.value);
				TransactionJsonMapper tx = new TransactionJsonMapper(ref, montant);
				log.info(tx.toString());
				result.add(tx);
			}
		}
		
		
		
		return result;
	}
	
	public Boolean transactionExist(String txid) {
		Boolean result = false;
		if(streams != null) {
			ReadOnlyKeyValueStore<String, Object> keyValueStore =
					streams.store(StoreQueryParameters.fromNameAndType(config.getTransactionStore(), 
							QueryableStoreTypes.keyValueStore()));
			
			
			Object tx = keyValueStore.get(txid);
			return tx != null;
			
		}
		return result;
	}
}
