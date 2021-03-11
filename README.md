# Apache Kafka Spring Boot client examples

This repository contains examples of Apache Kafka clients written using the Apache Kafka Java APIs:
* Message Producer which periodically produces messages into a topic
* Streams application which reads messages from a topic, transforms them (reverses the message payload) and sends them to another topic
* Consumer which is consuming messages from a topic

All examples are assembled into Docker images which allows them to be deployed on Kubernetes or OpenShift.

This repository contains `Deployments` for the clients as well as `KafkaTopic` and `KafkaUsers` for use by operators.


## Build

To build these examples you need some basic requirements.
Make sure you have `make`, `docker`, `JDK 11` and `mvn` installed. 
After cloning this repository to your folder Hello World example is fully ready to be build.
By one single command Java sources are compiled into JAR files, Docker images are created and pushed to repository.
By default the Docker organization to which images are pushed is the one defined by the `USER` environment variable which is assigned to the `DOCKER_ORG` one.
The organization can be changed exporting a different value for the `DOCKER_ORG` and it can also be the internal registry of an OpenShift running cluster.

The command for building the examples is:

```
make all
```

## Configuration

Although this Hello World is simple example it is fully configurable.
Below are listed and described environmental variables.

Producer  
* `BOOTSTRAP_SERVERS_URL` - host that is a list of Kafka broker addresses. The form of pair is `host`, e.g. `my-cluster-kafka-bootstrap` 
* `BOOTSTRAP_SERVERS_PORT` - host that is a list of Kafka broker addresses. The form of pair is `host`, e.g. `my-cluster-kafka-bootstrap` 
* `TOPIC` - the topic the producer will send to  
* `DELAY_MS` - the delay, in ms, between messages  
* `MESSAGE_COUNT` - the number of messages the producer should send  
* `CA_CRT` - the certificate of the CA which signed the brokers' TLS certificates, for adding to the client's trust store
* `USER_CRT` - the user's certificate
* `USER_KEY` - the user's private key
* `LOG_LEVEL` - logging level  
* `PRODUCER_ACKS` = acknowledgement level
* `HEADERS` = custom headers list separated by commas of `key1=value1, key2=value2`
* `ADDITIONAL_CONFIG` = additional configuration for a producer application. The form is `key=value` records separated by new line character
* `BLOCKING_PRODUCER` = if it's set, the producer will block another message until ack will be received
* `MESSAGES_PER_TRANSACTION` = how many messages will be part of one transaction. Transaction config could be set via `ADDITIONAL_CONFIG` variable. Default is 10.
* `SHEMA_REGISTRY_ENDPOINT` = schema registry URL.

### Generate AVRO Classes

To generate AVRO JAVA Source classes, run :
```
mvn generate-resources
```

