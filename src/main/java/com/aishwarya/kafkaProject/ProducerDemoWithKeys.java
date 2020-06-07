package com.aishwarya.kafkaProject;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerDemoWithKeys {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Logger LOG = LoggerFactory.getLogger(ProducerDemoWithKeys.class);
        //create producer properties
        String bootstrapServers = "localhost:9092";
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //create producer
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties);

        for(int i = 0 ;i < 10; i++) {

            String topic = "my-first-topic";
            String val = "Hello World!! "+ i;
            String key = "id_"+i;
            //create a producer record
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic,key,val);
            //send data
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    //on every successful record sent
                    if (e == null) {
                        LOG.info("Received Topic data : Topic[{}] , partition : [{}] , offset : [{}] , timestamp :[{}]", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());
                    } else {
                        LOG.error("Error while producing [{}]", e);
                    }
                }
            }).get(); //block send to make it sync ( not recommended )
        }

        //flush and close
        producer.flush();
        producer.close();
    }
}
