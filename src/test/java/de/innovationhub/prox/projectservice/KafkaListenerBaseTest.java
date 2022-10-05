package de.innovationhub.prox.projectservice;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class KafkaListenerBaseTest {
  public <K, V> ConsumerRecord<K, V> createDefaultConsumerRecord(
    K key, V value
  ) {
    return new ConsumerRecord<K, V>("", 0, 0, key, value);
  }
}
