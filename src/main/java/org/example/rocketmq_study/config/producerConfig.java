package org.example.rocketmq_study.config;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class producerConfig {
	@Bean
	public Producer rocketMQProducer() throws Exception {
		ClientServiceProvider provider = ClientServiceProvider.loadService();
		ClientConfiguration configuration = ClientConfiguration.newBuilder()
				.setEndpoints("192.168.111.130:8081")
				.build();
		return provider.newProducerBuilder()
				.setClientConfiguration(configuration)
				.build();
	}
}
