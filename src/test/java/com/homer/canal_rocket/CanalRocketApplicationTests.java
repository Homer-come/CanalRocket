package com.homer.canal_rocket;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CanalRocketApplicationTests {

  @Autowired
  private RocketMQTemplate rocketMQTemplate;

  @Test
  void contextLoads() throws Exception {
    DefaultMQProducer producer = new DefaultMQProducer("XIBA");
    producer.setNamesrvAddr("127.0.0.1:9876");
    producer.start();
    for (int i = 0; i < 10; i++) {
      Message message = new Message("canaltopic",("hello world"+i).getBytes());
      SendResult result = producer.send(message);
      System.out.println(result.toString());
    }
  }

  @Test
  void sendRocketMsg(){

  }
}
