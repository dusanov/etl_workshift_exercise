package me.dusanov.etl.workshift.etljobapp.kafka;

import lombok.RequiredArgsConstructor;
import me.dusanov.etl.workshift.etljobapp.dto.ShiftDto;
import me.dusanov.etl.workshift.etljobapp.model.Batch;
import me.dusanov.etl.workshift.etljobapp.model.ShiftCreatedMessage;
import me.dusanov.etl.workshift.etljobapp.service.WorkShiftClient;
import me.dusanov.etl.workshift.etljobapp.service.WorkShiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class ShiftCreatedMessageReceiver {

  private final Logger log = LoggerFactory.getLogger(ShiftCreatedMessageReceiver.class);
  private final WorkShiftService service;
    private final WorkShiftClient client;
  
  @KafkaListener(topics = "${spring.kafka.topic.shift-created-topic}")
  //@SendTo
  public void receive(@Payload ShiftCreatedMessage shiftCreated,
									 @Headers MessageHeaders headers) {

  	log.info("got this message: " + shiftCreated);
  	Batch batch = service.createNewBatch(new Batch());
  	ShiftDto shiftDto = client.get(batch, shiftCreated.getId());
    service.executeBatch(batch, Collections.singletonList(shiftDto));
  }
}