package me.dusanov.etl.workshift.workshiftendpoint.kafka;

import lombok.RequiredArgsConstructor;
import me.dusanov.etl.workshift.workshiftendpoint.model.ShiftCreatedMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Sender {
    private static final Logger log = LoggerFactory.getLogger(Sender.class);
    private static final int TIMEOUT = 1;

    @Value("${spring.kafka.topic.shift-created-topic}")
    private String shiftCreatedTopic;

    private final KafkaTemplate<String, ShiftCreatedMessage> template;

    public void send(ShiftCreatedMessage event){
        template.send(new ProducerRecord<String,ShiftCreatedMessage>(shiftCreatedTopic, event));
    }
}
