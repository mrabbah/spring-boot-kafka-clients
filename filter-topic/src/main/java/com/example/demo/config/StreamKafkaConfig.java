package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.streams.KafkaClientSupplier;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.common.serialization.Serdes;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingProducerInterceptor;
import io.opentracing.util.GlobalTracer;
import java.util.Properties;
import java.util.StringTokenizer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.opentracing.contrib.kafka.streams.TracingKafkaClientSupplier;
import io.opentracing.util.GlobalTracer;

@Component
@Log
public class StreamKafkaConfig {

	@Autowired
	private KafkaProperties config;
	
	private Properties props;
	
	private KafkaClientSupplier supplier;
	
	public Properties getProperties() {
		if(props == null) {
			props = new Properties();

	        props.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getApplicationId());
	        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
	        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, config.getCommitInterval());
	        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
	        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
	        
	        if (config.getAdditionalConfig() != null && 
	        		!config.getAdditionalConfig().isEmpty()) {
	            for (String configItem : config.getAdditionalConfig().split("\n")) {
	                String[] configuration = configItem.split("=");
	                props.put(configuration[0], configuration[1]);
	            }
	        }

	        if (config.getAdditionalConfig() != null && 
	        		!config.getAdditionalConfig().isEmpty()) {
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

	     // Cryptage communication
	        if (config.getTrustuedStorePassword() != null && config.getTrustedStorePath() != null)   {
	            log.info("Configuring truststore");
	            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
	            props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PKCS12");
	            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, config.getTrustuedStorePassword());
	            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, config.getTrustedStorePath());
	        }

	        // Authentification MTLS
	        if (config.getKeystorePassword() != null && config.getKeystorePath() != null)   {
	            log.info("Configuring keystore");
	            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
	            props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PKCS12");
	            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, config.getKeystorePassword());
	            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, config.getKeystorePath() );
	        }

	        // Authentification Oauth2
	        if ((config.getOauthAccessToken() != null)
	                || (config.getOauthEndPoint() != null && config.getOauthClientId() != null && config.getOauthRefreshToken() != null)
	                || (config.getOauthEndPoint() != null && config.getOauthClientId() != null && config.getOauthClientSecret() != null))    {
	            props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;");
	            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL".equals(props.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG)) ? "SASL_SSL" : "SASL_PLAINTEXT");
	            props.put(SaslConfigs.SASL_MECHANISM, "OAUTHBEARER");
	            props.put(SaslConfigs.SASL_LOGIN_CALLBACK_HANDLER_CLASS, "io.strimzi.kafka.oauth.client.JaasClientOauthLoginCallbackHandler");
	        }
	        
	        // Tracing distribuer en utilisant Jeager
	        if (config.getJeagerServiceName() != null)   {
	            Tracer tracer = Configuration.fromEnv().getTracer();
	            GlobalTracer.registerIfAbsent(tracer);
	            supplier = new TracingKafkaClientSupplier(tracer);
	        }
	        
	        if(config.getSchemaRegistryEndPoint() != null) {
	        	props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, 
	        			config.getSchemaRegistryEndPoint());
	        }
		}
		return props;
	}
	
	public KafkaClientSupplier getSupplier() {
		return this.supplier;
	}
}
