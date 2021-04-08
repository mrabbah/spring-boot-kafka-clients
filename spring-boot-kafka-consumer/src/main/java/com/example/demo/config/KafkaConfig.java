package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingProducerInterceptor;
import io.opentracing.util.GlobalTracer;
import java.util.Properties;
import java.util.StringTokenizer;


@Component
@Log
public class KafkaConfig {

	@Autowired
	private KafkaProperties config;
	
	private Properties props;
	
	public Properties getProperties() {
		if(props == null) {
			log.info("Creating Kafka consumer withe following configuration -> " + config.toString() );
			props = new Properties();
	        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
	        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getGroupId());
	        if (config.getClientRack() != null) {
	            props.put(ConsumerConfig.CLIENT_RACK_CONFIG, config.getClientRack());
	        }
	        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.getAutoOffsetReset());
	        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, config.getEnableAutoCommit());
	        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
	        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");

	        if (config.getAdditionalConfig() != null && !config.getAdditionalConfig().isEmpty()) {
	            StringTokenizer tok = new StringTokenizer(config.getAdditionalConfig(), ", \t\n\r");
	            while (tok.hasMoreTokens()) {
	                String record = tok.nextToken();
	                int endIndex = record.indexOf('=');
	                if (endIndex == -1) {
	                    throw new RuntimeException("Failed to parse Map from String");
	                }
	                String key = record.substring(0, endIndex);
	                String value = record.substring(endIndex + 1);
	                props.put(key.trim(), value.trim());
	            }
	        }

	        if (config.getTrustStorePassword() != null && config.getTrustStorePath() != null)   {
	            log.info("Configuring truststore");
	            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
	            props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PKCS12");
	            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, config.getTrustStorePassword());
	            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, config.getTrustStorePath());
	        }

	        if (config.getKeyStorePassword() != null && config.getKeyStorePath() != null)   {
	            log.info("Configuring keystore");
	            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
	            props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PKCS12");
	            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, config.getKeyStorePassword());
	            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, config.getKeyStorePath());
	        }

	        if ((config.getOauthAccessToken() != null)
	                || (config.getOauthTokenEndpointUri() != null && config.getOauthClientId() != null && config.getOauthRefreshToken() != null)
	                || (config.getOauthTokenEndpointUri() != null && config.getOauthClientId() != null && config.getOauthClientSecret() != null))    {
	            props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;");
	            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL".equals(props.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG)) ? "SASL_SSL" : "SASL_PLAINTEXT");
	            props.put(SaslConfigs.SASL_MECHANISM, "OAUTHBEARER");
	            props.put(SaslConfigs.SASL_LOGIN_CALLBACK_HANDLER_CLASS, "io.strimzi.kafka.oauth.client.JaasClientOauthLoginCallbackHandler");
	        }
	        
	        // Tracing distribuer en utilisant Jeager
	        if (config.getJeagerServiceName() != null)   {
	            Tracer tracer = Configuration.fromEnv().getTracer();
	            GlobalTracer.registerIfAbsent(tracer);

	            props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, TracingProducerInterceptor.class.getName());
	        }
	        
	        if(config.getSchemaRegistryEndPoint() != null) {
	        	props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, 
	        			config.getSchemaRegistryEndPoint());
	        	if(config.getSchemaregistrylogin() != null) {
	        		props.put(KafkaAvroSerializerConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
		        	props.put(KafkaAvroSerializerConfig.USER_INFO_CONFIG, 
		        			config.getSchemaregistrylogin() + ":" + config.getSchemaregistrypassword());
	        	}
	        	props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
	        }
		}
		return props;
	}
	
}
