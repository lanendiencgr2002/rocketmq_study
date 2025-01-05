package org.example.rocketmq_study;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.net.Socket;
import java.util.Collections;
import java.net.InetSocketAddress;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;

@SpringBootTest
public class firsttime_exampleTests {
    
    private static final String ENDPOINT = "192.168.111.130:8081";
    private static final String TOPIC = "TestTopic";

    @Test
    public void testRocketMQConnection() {
        try {
            System.out.println("开始测试RocketMQ连接");
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ENDPOINT, 8081), 1000);
            System.out.println("RocketMQ 连接成功");
            socket.close();
        } catch (Exception e) {
            System.out.println("RocketMQ 连接失败: " + e.getMessage());
            throw new RuntimeException("RocketMQ 服务器连接失败，请检查服务器是否启动");
        }
    }

    @Test
    public void testProduceMessage() {
        // 接入点地址，需要设置成Proxy的地址和端口列表，一般是xxx:8080;xxx:8081
        // 消息发送的目标Topic名称，需要提前创建
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfigurationBuilder builder = ClientConfiguration.newBuilder().setEndpoints(ENDPOINT);
        ClientConfiguration configuration = builder.build();
        Producer producer = null;
        try {
            // 初始化Producer时需要设置通信配置以及预绑定的Topic。
            producer = provider.newProducerBuilder()
                .setTopics(TOPIC)
                .setClientConfiguration(configuration)
                .build();
        } catch (ClientException e) {
            System.out.println("生产者创建失败: " + e);
            throw new RuntimeException(e);
        }
        // 普通消息发送
        Message message = provider.newMessageBuilder()
            .setTopic(TOPIC)
            // 设置消息索引键，可根据关键字精确查找某条消息。
            .setKeys("messageKey")
            // 设置消息Tag，用于消费端根据指定Tag过滤消息。
            .setTag("messageTag")
            // 消息体。
            .setBody("测试消息1".getBytes())
            .build();
        try {
            // 发送消息，需要关注发送结果，并捕获失败等异常。
            SendReceipt sendReceipt = producer.send(message);
            System.out.println("发送消息成功, messageId={}" + sendReceipt.getMessageId());
        } catch (ClientException e) {
            System.out.println("发送消息失败: " + e);
        }
        // producer.close();
    }

    @Test
    @SuppressWarnings("unused")
    public void testConsumeMessage() throws ClientException, InterruptedException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
            .setEndpoints(ENDPOINT)
            .build();
        // 订阅消息的过滤规则，表示订阅所有Tag的消息。
        String tag = "*";
        FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);
        // 为消费者指定所属的消费者分组，Group需要提前创建。
        String consumerGroup = "TestConsumerGroup";
        // 指定需要订阅哪个目标Topic，Topic需要提前创建。
        // 初始化PushConsumer，需要绑定消费者分组ConsumerGroup、通信参数以及订阅关系。
        PushConsumer pushConsumer = provider.newPushConsumerBuilder()
            .setClientConfiguration(clientConfiguration)
            // 设置消费者分组。
            .setConsumerGroup(consumerGroup)
            // 设置预绑定的订阅关系。
            .setSubscriptionExpressions(Collections.singletonMap(TOPIC, filterExpression))
            // 设置消费监听器。
            .setMessageListener(messageView -> {
                // 处理消息并返回消费结果。
                System.out.println("消费消息成功, 消息id={}" + messageView.getMessageId());
                // 正确处理 ByteBuffer 中的消息内容
                byte[] bytes = new byte[messageView.getBody().remaining()];
                messageView.getBody().get(bytes);
                String messageContent = new String(bytes);
                System.out.println("消费消息成功, 消息内容=" + messageContent);
                return ConsumeResult.SUCCESS;
            })
            .build();
        Thread.sleep(Long.MAX_VALUE);
        // 如果不需要再使用 PushConsumer，可关闭该实例。
        // pushConsumer.close();
    }

   



    
} 