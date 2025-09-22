package com.example.transaction_service.kafkaConfig;

import com.example.transaction_service.model.TransactionCompletedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    public static final String TOPIC ="transaction_service";


    @Autowired
    KafkaTemplate<String, TransactionCompletedEvent> kafkaTemplate;

    @Autowired
    KafkaTemplate<String, String> stringKafkaTemplate;

    public void publishEvent(TransactionCompletedEvent event){
        kafkaTemplate.send(TOPIC,event);
    }

    public void notification(String message){
        stringKafkaTemplate.send(TOPIC,message);
    }


}
