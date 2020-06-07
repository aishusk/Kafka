package com.aishwarya.kafkaProject;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.record.Record;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemo {
    public static void main(String[] args) {
        Logger LOG = LoggerFactory.getLogger(ConsumerDemo.class);
        String TOPIC_NAME = "my-first-topic";
        String bootstrapServers = "localhost:9092";
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,"my-first-group");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        //create consumer
        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(properties);

        //subscribe consumer
        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        //poll for new data
        while(true){
          ConsumerRecords<String, String> record = consumer.poll(Duration.ofMillis(100));
          for(ConsumerRecord<String,String> record1 : record){
            LOG.info("key :[{}] value :[{}]",record1.key(),record1.value());
            LOG.info("Partition:[{}] offset:[{}]",record1.partition(),record1.offset());
          }
        }
    }
}
