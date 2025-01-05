package org.example.rocketmq_study.messages;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.java.message.MessageBuilderImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
public class normal_messagesTests {

	@Autowired
	private Producer producer;

	@Test
	public void testcontextLoads() {
		System.out.println("开始测试RocketMQ消息");
		System.out.println("RocketMQ 连接成功");
	}

	@Test
	public void testSendMessage() throws Exception {
		//普通消息发送
		MessageBuilder messageBuilder = new MessageBuilderImpl();
		Message message = messageBuilder.setTopic("TestTopic")
			.setKeys("messageKey")
			.setTag("messageTag")
			.setBody("messageBody".getBytes())
			.build();
		
		try {
			SendReceipt sendReceipt = producer.send(message);
			System.out.println(sendReceipt.getMessageId());
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
}
