FROM apache/kafka:latest

COPY target/*.jar /opt/kafka/libs/
COPY config/*.properties /opt/kafka/config/

WORKDIR /opt/kafka