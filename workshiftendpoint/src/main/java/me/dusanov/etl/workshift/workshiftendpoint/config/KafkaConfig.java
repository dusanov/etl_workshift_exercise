package me.dusanov.etl.workshift.workshiftendpoint.config;

import me.dusanov.etl.workshift.workshiftendpoint.model.ShiftCreatedMessage;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.topic.shift-created-topic}")
	private String shiftCreatedTopic;

//	@Value("${spring.kafka.topic.shift-created-topic-reply}")
//	private String shiftCreatedReplyTopic;
	  
	@Value("${spring.kafka.consumer-group}")
	private String consumerGroup;
	
	@Bean
	public Map<String, Object> producerConfigs() {
	    Map<String, Object> props = new HashMap<>();
	    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
	    return props;
	}	
	
	@Bean
	public Map<String, Object> consumerConfigs() {
	    Map<String, Object> props = new HashMap<>();
	    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
	    props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
	    return props;
	}

	@Bean
	public ProducerFactory<String, ShiftCreatedMessage> producerFactory() {
	    return new DefaultKafkaProducerFactory<>(producerConfigs());
	}
	  
	@Bean
	public KafkaTemplate<String, ShiftCreatedMessage> kafkaTemplate() {
	    return new KafkaTemplate<>(producerFactory());
	}	
	
	@Bean
	public ConsumerFactory<String, ShiftCreatedMessage> consumerFactory() {
	    return new DefaultKafkaConsumerFactory<>(consumerConfigs(),new StringDeserializer(),new JsonDeserializer<>(ShiftCreatedMessage.class));
	}	
	
	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ShiftCreatedMessage>> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, ShiftCreatedMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setReplyTemplate(kafkaTemplate());
		return factory;
	}

	/*
    @Bean
    public ReplyingKafkaTemplate<String, ShiftCreatedMessage, ShiftCreatedMessage> replyingTemplate(
            ProducerFactory<String, ShiftCreatedMessage> pf,
            GenericMessageListenerContainer<String, ShiftCreatedMessage> repliesContainer) {
    	
        return new ReplyingKafkaTemplate<String, ShiftCreatedMessage, ShiftCreatedMessage>(pf, repliesContainer);
    }


    @Bean
    public ConcurrentMessageListenerContainer<String, ShiftCreatedMessage> repliesContainer(
            KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ShiftCreatedMessage>>  kafkaListenerContainerFactory ) {

        ConcurrentMessageListenerContainer<String, ShiftCreatedMessage> repliesContainer = kafkaListenerContainerFactory.createContainer(shiftCreatedReplyTopic);
        repliesContainer.getContainerProperties().setGroupId("repliesGroup");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }	
*/
	@Bean
	public NewTopic shiftCreatedTopic() {
		return TopicBuilder.name(shiftCreatedTopic)
            .partitions(1)
            .replicas(1)
            //.compact()
            .build();
	}	
/*
	@Bean
	public NewTopic shiftCreatedReplyTopic() {
		return TopicBuilder.name(shiftCreatedReplyTopic)
            .partitions(1)
            .replicas(1)
            //.compact()
            .build();
	}	
*/
}
