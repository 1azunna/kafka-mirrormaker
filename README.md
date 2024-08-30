# kafka-mirrormaker

## Configuration

### TopicRemappingPolicy

Add the following to the mirrormaker properties file

```
replication.policy.class = com.zunnet.mirrormaker.replicationpolicies.TopicRemappingPolicy
replication.policy.topics.regex-patterns = <regex_pattern|replacement> # Replacements support using regex matching group variables e.g replacement_$1_$2
```

## Setup

1. Build the Jar file

```bash
mvn clean verify
```

2. Run docker compose up

```bash
docker compose up --build
```

3. Create topic and messages on kafka1 container

```bash
docker exec --workdir /opt/kafka/bin/ -it kafka1 sh 

./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic <topic name>
./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic <topic name>
>test
>message
>^C
```

4. View replicated topic and messages on kafka2 container

```bash
docker exec --workdir /opt/kafka/bin/ -it kafka2 sh 

./kafka-topics.sh --bootstrap-server localhost:9093 --list
<replicated topic name>
./kafka-console-consumer.sh  --bootstrap-server localhost:9093 --topic <replicated topic name> --from-beginning
```

If there are any issues with topic replication, check mirrormaker container logs.