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
	@Value("${groupid}")
    private String groupId;
	@Value("${autooffsetreset}")
    private String autoOffsetReset; //"earliest" or //latest
	@Value("${enableautocommit}")
    private String enableAutoCommit;
	@Value("${clientrack}")
    private String clientRack;
    @Value("${trustuedstorepassword}")
    private String trustStorePassword;
    @Value("${trustedstorepath}")
    private String trustStorePath;
    @Value("${keystorepassword}")
    private String keyStorePassword;
    @Value("${keystorepath}")
    private String keyStorePath;
    @Value("${oauthclientid}")
    private String oauthClientId;
    @Value("${oauthclientsecret}")
    private String oauthClientSecret;
    @Value("${oauthaccesstoken}")
    private String oauthAccessToken;
    @Value("${oauthrefreshtoken}")
    private String oauthRefreshToken;
    @Value("${oauthendpoint}")
    private String oauthTokenEndpointUri;
    @Value("${additionalconfig}")
    private String additionalConfig;
	@Value("${jeagerservicename}")
	private String jeagerServiceName;
	@Value("${schemaregistryendpoint}")
	private String schemaRegistryEndPoint;
	@Value("${schemaregistrylogin}")
	private String schemaregistrylogin;
	@Value("${schemaregistrypassword}")
	private String schemaregistrypassword;
	
}
