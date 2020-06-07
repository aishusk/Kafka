package com.aishwarya.kafkaProject;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerThreadDemo {
    public static void main(String[] args) {
        new ConsumerThreadDemo().run();
    }

    private void run() {
        Logger LOG = LoggerFactory.getLogger(ConsumerThreadDemo.class);
        String TOPIC_NAME = "my-first-topic";
        String bootstrapServers = "localhost:9092";

        CountDownLatch latch = new CountDownLatch(1);
        LOG.info("creating consumer");
        Runnable consumer = new ConsumerThread(latch, TOPIC_NAME, "my-first-group",bootstrapServers);
        Thread myConsumerThread = new Thread(consumer);
        myConsumerThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            LOG.info("Shutdown hook called");
            ((ConsumerThread)consumer).shutdown();
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.error("App is interrupted");
        }finally {
            LOG.info("App is closing ");
        }
    }//poll for new data


    public class ConsumerThread implements Runnable{
        Logger LOG = LoggerFactory.getLogger(ConsumerThread.class);
        private CountDownLatch latch;
        private KafkaConsumer<String, String> consumer;

        public ConsumerThread(CountDownLatch latch, String topic,
                              String groupid, String bootstrapServers){
            this.latch=latch;
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupid);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
            consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(Arrays.asList(topic));
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ConsumerRecords<String, String> record = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record1 : record) {
                        LOG.info("key :[{}] value :[{}]", record1.key(), record1.value());
                        LOG.info("Partition:[{}] offset:[{}]", record1.partition(), record1.offset());
                    }
                }
            }catch (WakeupException e){
                LOG.error("Exception occured in poll [{}]",e);
            }finally {
                consumer.close();
                latch.countDown();
            }
        }

        public void shutdown(){
            consumer.wakeup();
        }
    }
}

