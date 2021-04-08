package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;


@PropertySource({"classpath:kafka.properties"})
@Data
@Component
public class KafkaProperties {

	@Value("${bootstrapservers}")
	private String bootstrapServers;
	@Value("${topic}")
	private String topic;
	@Value("${trustuedstorepassword}")
	private String trustuedStorePassword;
	@Value("${trustedstorepath}")
	private String trustedStorePath;
	@Value("${keystorepassword}")
	private String keystorePassword;
	@Value("${keystorepath}")
	private String keystorePath;
	@Value("${oauthclientid}")
	private String oauthClientId;
	@Value("${oauthclientsecret}")
	private String oauthClientSecret;
	@Value("${oauthaccesstoken}")
	private String oauthAccessToken;
	@Value("${oauthrefreshtoken}")
	private String oauthRefreshToken;
	@Value("${oauthendpoint}")
	private String oauthEndPoint;
	@Value("${acks}")
	private String acks;
	@Value("${headers}")
	private String headers;
	@Value("${additionalconfig}")
	private String additionalConfig;
	@Value("${jeagerservicename}")
	private String jeagerServiceName;
	@Value("${blockingproducer}")
	private String blockingProducer;
	@Value("${messagespertransaction}")
	private int messagesPerTransaction;
	@Value("${schemaregistryendpoint}")
	private String schemaRegistryEndPoint;
	@Value("${schemaregistrylogin}")
	private String schemaregistrylogin;
	@Value("${schemaregistrypassword}")
	private String schemaregistrypassword;
	
}
