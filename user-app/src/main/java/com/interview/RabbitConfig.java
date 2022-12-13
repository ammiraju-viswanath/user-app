package com.interview;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	public static final String QUEUE_NAME = "queue-name";
	public static final String EXCAHNGE_NAME = "excahnge-name";
	public static final String ROUTING_KEY = "routing-key";
	@Bean
	public Binding bindingConfig() {
		return BindingBuilder.bind(extractedQueue()).
				to(extractedTopic()).with(RabbitConfig.ROUTING_KEY);

	}
	@Bean
	public Queue extractedQueue() {
		return new Queue(RabbitConfig.QUEUE_NAME);
	}
	@Bean
	public TopicExchange extractedTopic() {
		return new TopicExchange(RabbitConfig.EXCAHNGE_NAME);
	}
	@Bean
	public Jackson2JsonMessageConverter messageConvertor() {
		return new Jackson2JsonMessageConverter();
	}
	@Bean
	public RabbitTemplate template(ConnectionFactory cf) {
		final var template = new RabbitTemplate(cf);
		template.setMessageConverter(messageConvertor());
		return template;

	}


}
