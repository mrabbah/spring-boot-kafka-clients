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
	@Value("${applicationid}")
    private String applicationId;
	@Value("${sourcetopic}")
    private String sourceTopic;
	@Value("${tragettopic}")
    private String targetTopic;
	@Value("${commitintervalms}")
    private int commitInterval;
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
