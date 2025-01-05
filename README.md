消息消费模式 - 推拉模式(Push/Pull)

推模式(Push)：

- 由Broker主动将消息推送给Consumer
- Consumer被动接收消息
- 实时性较好
- 消费者负载由Broker控制
- 实现较为简单

拉模式(Pull)：

- Consumer主动从Broker拉取消息
- 消费者可以按需获取消息
- 实时性相对较差
- 消费者可以自主控制负载
- 实现相对复杂

特点：
主动拉取消息
可以控制消费速率
需要手动确认消息
适合批量处理场景
消费者可以自主控制负载

选择建议：
根据不同场景选择合适的模式：
选择PushConsumer的场景：

- 实时性要求高
- 消息量比较稳定
- 想要简单的消费代码
- 不需要特别的流控

选择SimpleConsumer的场景：

- 需要批量处理消息
- 需要精确控制消费速率
- 消息处理逻辑复杂
- 需要自定义重试策略

Topic和Broker的区别：

Broker（消息服务器）：

- 是RocketMQ的服务器实体
- 负责消息的存储和转发
- 可以部署多个Broker形成集群
- 通常采用主从架构保证高可用
- 物理概念
- 提供消息存储服务
- 管理Topic和队列
- 处理消息持久化

Topic（主题）：

- 是消息的逻辑分类
- 一个Topic可以分布在多个Broker上
- 用于区分不同类型的消息
- 可以理解为消息的归类标签
- 逻辑概念
- 消息的分类标识
- 支持消息过滤
- 便于消息管理

使用建议：

- 合理规划Topic数量
- 根据业务类型划分Topic
- 适当配置Broker集群
- 注意Topic的队列数设置
- 监控Topic和Broker状态

# 使用mqadmin 
docker exec -it rmqbroker bash
cd /home/rocketmq/rocketmq-5.3.1/bin
sh mqadmin updateTopic -n <nameserver_address> -t <topic_name> -c <cluster_name> -a +message.type=DELAY
```markdown
# 创建延迟主题
/bin/mqadmin updateTopic -c DefaultCluster -t DelayTopic -n 192.168.111.130:9876 -a +message.type=DELAY

```

# compose命令
先建broker.conf在当前目录 brokerIP1=192.168.78.150
然后文件名：docker-compose.yml 命令：docker compose up -d 
version: '3.8'
services:
  namesrv:
    image: apache/rocketmq:5.3.1
    container_name: rmqnamesrv
    ports:
      - 9876:9876
    networks:
      - rocketmq
    command: sh mqnamesrv
  broker:
    image: apache/rocketmq:5.3.1
    container_name: rmqbroker
    ports:
      - 10909:10909
      - 10911:10911
      - 10912:10912
    environment:
      - NAMESRV_ADDR=rmqnamesrv:9876
      - JAVA_OPT_EXT=-server -Xms256m -Xmx256m  #修改jvm内存初始化最大值
    depends_on:
      - namesrv
    volumes:
      - ./broker.conf:/home/rocketmq/broker.conf #配置文件
    networks:
      - rocketmq
    command: sh mqbroker -c /home/rocketmq/broker.conf #应用配置
  proxy:
    image: apache/rocketmq:5.3.1
    container_name: rmqproxy
    networks:
      - rocketmq
    depends_on:
      - broker
      - namesrv
    ports:
      - 8080:8080
      - 8081:8081
    restart: on-failure
    environment:
      - NAMESRV_ADDR=rmqnamesrv:9876
    command: sh mqproxy
  dashboard:
    image: apacherocketmq/rocketmq-dashboard:latest
    container_name: rmqdashboard
    ports:
      - 8088:8080
    environment:
      - JAVA_OPTS=-Drocketmq.namesrv.addr=rmqnamesrv:9876
    networks:
      - rocketmq
    depends_on:
      - namesrv
    restart: on-failure
networks:
  rocketmq:
    driver: bridge