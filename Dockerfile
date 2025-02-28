FROM apache/kafka:3.8.0

COPY target/*.jar /opt/kafka/libs/
COPY config/*.properties /opt/kafka/config/

WORKDIR /opt/kafka