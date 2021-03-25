FROM centos:7

RUN yum -y update && yum -y install java-11-openjdk-headless openssl && yum -y clean all

# Set JAVA_HOME env var
ENV JAVA_HOME /usr/lib/jvm/java

ARG version=latest
ENV VERSION ${version}

COPY ./scripts/ /bin

ADD target/spring-boot-kafka-producer-0.0.1-SNAPSHOT.jar /

CMD ["/bin/run.sh", "/spring-boot-kafka-producer-0.0.1-SNAPSHOT.jar"]