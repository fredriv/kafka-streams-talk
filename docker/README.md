# Kafka Services

This docker-compose provides:

- [ZooKeeper](https://hub.docker.com/r/confluentinc/cp-zookeeper/)
- [Kafka](https://hub.docker.com/r/confluentinc/cp-kafka/)
- [Kafka Schema Registry](https://hub.docker.com/r/confluentinc/cp-schema-registry/)
- [Kafka Manager](https://hub.docker.com/r/sheepkiller/kafka-manager/)

## Usage with docker native in MacOS

Add the following line to your /etc/hosts

```
127.0.0.1 zk kafka schema
```

Run docker-compose

```bash
docker-compose -f kafka-mac.yaml up
```

Now you can start sending events to the `Private-LocalDev-Yggdrasil-DataCollectorFirehose-1` topic:
```
cat events.json | docker run --net=host --rm -i confluentinc/cp-kafka:latest kafka-console-producer \
    --request-required-acks 0 \
    --batch-size 100 \
    --producer-property request.timeout.ms=600000 \
    --broker-list localhost:29092 \
    --topic Private-LocalDev-Yggdrasil-DataCollectorFirehose-1
```

Start in another terminal a consumer to see all metrics:
```
docker run -ti --net=host --rm confluentinc/cp-kafka:latest kafka-console-consumer  \
    --bootstrap-server localhost:29092 \
    --topic Public-LocalDev-Yggdrasil-DataQuality-Metrics-1 \
    --from-beginning
```


## Kafka command examples

# Create a topic

```bash
docker run --net=host --rm confluentinc/cp-kafka:latest \
  kafka-topics --create --topic test --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:32181
```

# Check a topic

```bash
docker run --net=host --rm confluentinc/cp-kafka:latest \
  kafka-topics --describe --topic test --zookeeper localhost:32181
```

# List topics

```bash
docker run --net=host --rm confluentinc/cp-kafka:latest \
  kafka-topics --list --zookeeper localhost:32181
```

# Console producer

```bash
docker run -ti --net=host --rm confluentinc/cp-kafka:latest \
  kafka-console-producer --request-required-acks 1 --broker-list localhost:29092 --topic events
```

```bash
gzcat part-00000.gz | docker run --net=host --rm confluentinc/cp-kafka:latest kafka-console-producer --request-required-acks 1 --broker-list localhost:29092 --topic events
```
